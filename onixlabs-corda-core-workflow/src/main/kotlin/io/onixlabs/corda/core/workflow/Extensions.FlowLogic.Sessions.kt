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
 * @throws FlowException if a required counter-party session is missing for a state participant.
 */
@Suspendable
fun FlowLogic<*>.checkSufficientSessions(sessions: Iterable<FlowSession>, states: Iterable<ContractState>) {
    val stateCounterparties = states
        .flatMap { it.participants }
        .filter { it !in serviceHub.myInfo.legalIdentities }
        .toSet()

    val sessionCounterparties = sessions
        .map { it.counterparty }
        .toSet()

    stateCounterparties.forEach {
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
 * @throws FlowException if a required counter-party session is missing for a state participant.
 */
@Suspendable
fun FlowLogic<*>.checkSufficientSessions(sessions: Iterable<FlowSession>, vararg states: ContractState) {
    checkSufficientSessions(sessions, states.toSet())
}

/**
 * Checks that sufficient flow sessions have been provided for the specified transaction.
 *
 * @param sessions The flow sessions that have been provided to the flow.
 * @param transaction The transaction for which to check that sufficient flow sessions exist.
 * @throws FlowException if a required counter-party session is missing for a state participant.
 */
@Suspendable
fun FlowLogic<*>.checkSufficientSessions(sessions: Iterable<FlowSession>, transaction: TransactionBuilder) {
    val ledgerTransaction = transaction.toLedgerTransaction(serviceHub)
    checkSufficientSessions(sessions, ledgerTransaction.inputStates + ledgerTransaction.outputStates)
}
