package io.onixlabs.corda.core.workflow.messaging.flows

import co.paralleluniverse.fibers.Suspendable
import io.onixlabs.corda.core.workflow.Message
import io.onixlabs.corda.core.workflow.ReceiveMessageFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.identity.Party

@InitiatedBy(SendFlow::class)
class ReceiveFlow<T>(
    private val session: FlowSession
) : FlowLogic<Pair<Party, T>>() where T : Message<*> {

    @Suspendable
    override fun call(): Pair<Party, T> {
        return subFlow(ReceiveMessageFlow(session, true))
    }
}
