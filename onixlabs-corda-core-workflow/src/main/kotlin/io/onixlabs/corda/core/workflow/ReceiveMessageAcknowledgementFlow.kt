package io.onixlabs.corda.core.workflow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.core.utilities.unwrap

/**
 * Represents a sub-flow that receives a message acknowledgement from the specified counter-party.
 *
 * @param session The flow session with the counter-party who is sending the message acknowledgement.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class ReceiveMessageAcknowledgementFlow<T>(
    private val session: FlowSession,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Pair<Party, T>>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(RECEIVING)

        private object RECEIVING : Step("Receiving message acknowledgement from counter-party.")
    }

    @Suspendable
    @Suppress("UNCHECKED_CAST")
    override fun call(): Pair<Party, T> {
        currentStep(RECEIVING, additionalLogInfo = session.counterparty.toString())
        return session.counterparty to session.receive<MessageAcknowledgement>().unwrap { it as T }
    }

    /**
     * Represents a flow initiated by [SendMessageAcknowledgementFlow.Initiator] and receives a message acknowledgement.
     *
     * @param session The flow session with the counter-party who is sending the message acknowledgement.
     */
    @InitiatedBy(SendMessageAcknowledgementFlow.Initiator::class)
    class Receiver<T>(private val session: FlowSession) : FlowLogic<Pair<Party, T>>() where T : MessageAcknowledgement {

        private companion object {
            object RECEIVING : Step("Receiving message acknowledgement.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(RECEIVING)

        @Suspendable
        override fun call(): Pair<Party, T> {
            currentStep(RECEIVING)
            return subFlow(ReceiveMessageAcknowledgementFlow(session, RECEIVING.childProgressTracker()))
        }
    }
}
