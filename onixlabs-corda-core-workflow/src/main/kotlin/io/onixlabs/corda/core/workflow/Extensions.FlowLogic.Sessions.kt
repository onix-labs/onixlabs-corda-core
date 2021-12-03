/*
 * Copyright 2020-2021 ONIXLabs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.identity.AbstractParty
import net.corda.core.transactions.TransactionBuilder

/**
 * Initiates flow sessions for the specified parties, except for identities that belong to the local node,
 * since flow sessions are not required locally.
 *
 * @param parties The parties for which to create flow sessions.
 * @return Returns a set of flow sessions.
 */
@Suspendable
fun FlowLogic<*>.initiateFlows(vararg parties: AbstractParty): Set<FlowSession> {
    return parties
        .map { serviceHub.identityService.requireWellKnownPartyFromAnonymous(it) }
        .filter { it !in serviceHub.myInfo.legalIdentities }
        .map { initiateFlow(it) }
        .toSet()
}

/**
 * Initiates flow sessions for the participants of the specified states, except for
 * identities that belong to the local node, since flow sessions are not required locally.
 *
 * @param states The states for which to create flow sessions for the state participants.
 * @return Returns a set of flow sessions.
 */
@Suspendable
fun FlowLogic<*>.initiateFlows(vararg states: ContractState): Set<FlowSession> {
    return states.flatMap { it.participants }
        .map { serviceHub.identityService.requireWellKnownPartyFromAnonymous(it) }
        .filter { it !in serviceHub.myInfo.legalIdentities }
        .map { initiateFlow(it) }
        .toSet()
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
 * Checks that sufficient flow sessions have been provided for the specified states.
 *
 * Assuming that the specified states will be used as input or output states in a transaction, this function will
 * extract a set of all state participants, excluding identities owned by the initiating node, and then check
 * that a flow session exists for each participant.
 *
 * @param sessions The flow sessions that have been provided to the flow.
 * @param states The states that will be used as input or output states in a transaction.
 * @param partyProjection Provides a mechanism to project, or resolve anonymous to well-known identities.
 * @throws FlowException if a required counter-party session is missing for a state participant.
 */
@Suspendable
fun FlowLogic<*>.checkSufficientSessions(
    sessions: Iterable<FlowSession>,
    states: Iterable<ContractState>,
    partyProjection: (AbstractParty) -> AbstractParty = { it }
) {
    val stateCounterparties = states
        .flatMap { it.participants }
        .filter { it !in serviceHub.myInfo.legalIdentities }
        .toSet()

    val sessionCounterparties = sessions
        .map { it.counterparty }
        .toSet()

    stateCounterparties.map { partyProjection(it) }.forEach {
        if (it !in sessionCounterparties) {
            throw FlowException("A flow session must be provided for the specified counter-party: $it.")
        }
    }
}

/**
 * Checks that sufficient flow sessions have been provided for the specified states.
 *
 * Assuming that the specified states will be used as input or output states in a transaction, this function will
 * extract a set of all state participants, excluding identities owned by the initiating node, and then check
 * that a flow session exists for each participant.
 *
 * @param sessions The flow sessions that have been provided to the flow.
 * @param states The states that will be used as input or output states in a transaction.
 * @param partyProjection Provides a mechanism to project, or resolve anonymous to well-known identities.
 * @throws FlowException if a required counter-party session is missing for a state participant.
 */
@Suspendable
fun FlowLogic<*>.checkSufficientSessions(
    sessions: Iterable<FlowSession>, vararg states: ContractState,
    partyProjection: (AbstractParty) -> AbstractParty = { it }
) = checkSufficientSessions(sessions, states.toSet(), partyProjection)

/**
 * Checks that sufficient flow sessions have been provided for the specified transaction.
 *
 * @param sessions The flow sessions that have been provided to the flow.
 * @param transaction The transaction for which to check that sufficient flow sessions exist.
 * @param partyProjection Provides a mechanism to project, or resolve anonymous to well-known identities.
 * @throws FlowException if a required counter-party session is missing for a state participant.
 */
@Suspendable
fun FlowLogic<*>.checkSufficientSessions(
    sessions: Iterable<FlowSession>,
    transaction: TransactionBuilder,
    partyProjection: (AbstractParty) -> AbstractParty = { it }
) {
    val ledgerTransaction = transaction.toLedgerTransaction(serviceHub)
    checkSufficientSessions(sessions, ledgerTransaction.inputStates + ledgerTransaction.outputStates, partyProjection)
}
