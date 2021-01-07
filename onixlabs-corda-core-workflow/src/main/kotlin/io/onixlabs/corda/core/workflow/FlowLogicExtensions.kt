/**
 * Copyright 2020 Matthew Layton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.onixlabs.corda.core.workflow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.cordapp.CordappConfigException
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.internal.randomOrNull
import net.corda.core.node.ServiceHub
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker.Step

/**
 * Gets the first available notary.
 */
val FlowLogic<*>.firstNotary: Party
    get() = serviceHub.networkMapCache.notaryIdentities.firstOrNull()
        ?: throw NoSuchElementException("No available notaries.")

/**
 * Gets a randomly available notary.
 */
val FlowLogic<*>.randomNotary: Party
    get() = serviceHub.networkMapCache.notaryIdentities.randomOrNull()
        ?: throw NoSuchElementException("No available notaries.")

/**
 * Gets the preferred notary from the node config, or alternatively a default notary in the event that
 * a preferred notary has not been specified in the node config.
 *
 * @param defaultSelector The selector function to obtain a notary if none have been specified in the node config.
 * @return Returns the preferred or default notary.
 * @throws IllegalAccessException If the preferred notary cannot be found in the network map cache.
 */
@Suspendable
fun FlowLogic<*>.getPreferredNotary(defaultSelector: (ServiceHub) -> Party = { firstNotary }): Party = try {
    val name = CordaX500Name.parse(serviceHub.getAppContext().config.getString("notary"))
    serviceHub.networkMapCache.getNotary(name) ?: throw IllegalArgumentException(
        "Notary with the specified name cannot be found in the network map cache: $name."
    )
} catch (e: CordappConfigException) {
    defaultSelector(serviceHub)
}

/**
 * Sets the current progress tracker step.
 *
 * @param step The progress tracker step.
 * @param log Determines whether to log the step.
 * @param additionalLogInfo Additional information to log when setting the current step.
 */
@Suspendable
fun FlowLogic<*>.currentStep(step: Step, log: Boolean = true, additionalLogInfo: String? = null) {
    progressTracker?.currentStep = step

    if (log) {
        if (additionalLogInfo.isNullOrBlank()) {
            logger.info(step.label)
        } else {
            logger.info("${step.label} ($additionalLogInfo)")
        }
    }
}

/**
 * Initiates flow sessions for the specified parties and participants of the specified states, except for
 * identities that belong to the local node, since flow sessions are not required locally.
 *
 * @param parties The parties for which to create flow sessions.
 * @param states The states for which to create flow sessions for the state participants.
 * @return Returns a set of flow sessions.
 */
@Suspendable
fun FlowLogic<*>.initiateFlows(parties: Iterable<AbstractParty>, vararg states: ContractState): Set<FlowSession> {
    return (parties + states.flatMap { it.participants })
        .map { serviceHub.identityService.requireWellKnownPartyFromAnonymous(it) }
        .filter { it !in serviceHub.myInfo.legalIdentities }
        .map { initiateFlow(it) }
        .toSet()
}

/**
 * Finds a recorded transaction in the vault for the specified transaction hash.
 *
 * @param transactionHash The transaction hash of the transaction to find.
 * @return Returns a [SignedTransaction] for the specified transaction hash.
 * @throws FlowException If a signed Transaction cannot be found for the specified transaction hash.
 *
 */
@Suspendable
fun FlowLogic<*>.findTransaction(transactionHash: SecureHash): SignedTransaction {
    return serviceHub.validatedTransactions.getTransaction(transactionHash)
        ?: throw FlowException("Did not find a transaction with the specified hash: $transactionHash")
}

/**
 * Finds a recorded transaction in the vault for the specified transaction hash.
 *
 * @param stateRef The state reference of the transaction to find.
 * @return Returns a [SignedTransaction] for the specified transaction hash.
 * @throws FlowException If a signed Transaction cannot be found for the specified transaction hash.
 *
 */
@Suspendable
fun FlowLogic<*>.findTransaction(stateRef: StateRef): SignedTransaction {
    return findTransaction(stateRef.txhash)
}

/**
 * Finds a recorded transaction in the vault for the specified transaction hash.
 *
 * @param stateAndRef The state and reference of the transaction to find.
 * @return Returns a [SignedTransaction] for the specified transaction hash.
 * @throws FlowException If a signed Transaction cannot be found for the specified transaction hash.
 *
 */
@Suspendable
fun FlowLogic<*>.findTransaction(stateAndRef: StateAndRef<*>): SignedTransaction {
    return findTransaction(stateAndRef.ref)
}
