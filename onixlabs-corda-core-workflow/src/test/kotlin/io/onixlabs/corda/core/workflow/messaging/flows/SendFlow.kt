package io.onixlabs.corda.core.workflow.messaging.flows

import co.paralleluniverse.fibers.Suspendable
import io.onixlabs.corda.core.workflow.initiateFlows
import io.onixlabs.corda.core.workflow.Message
import io.onixlabs.corda.core.workflow.MessageAcknowledgement
import io.onixlabs.corda.core.workflow.SendMessageFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.identity.Party

@InitiatingFlow
class SendFlow<T>(
    private val message: T,
    private val counterparties: Set<Party>
) : FlowLogic<Map<Party, MessageAcknowledgement>>() where T : Message<*> {

    @Suspendable
    override fun call(): Map<Party, MessageAcknowledgement> {
        val sessions = initiateFlows(counterparties)
        return subFlow(SendMessageFlow(message, sessions, true))
    }
}
