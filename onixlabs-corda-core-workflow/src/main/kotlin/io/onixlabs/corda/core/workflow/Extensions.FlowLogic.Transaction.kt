/**
 * Copyright 2020-2021 Matthew Layton
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
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker.Step

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
