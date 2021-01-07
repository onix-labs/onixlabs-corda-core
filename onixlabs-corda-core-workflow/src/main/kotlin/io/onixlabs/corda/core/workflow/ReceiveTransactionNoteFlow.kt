package io.onixlabs.corda.core.workflow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.core.utilities.unwrap

/**
 * Represents a sub-flow that receives a transaction note from the specified counter-party.
 *
 * @param session The flow session with the counter-party who is sending the transaction note.
 * @param persist Determines whether the transaction note should be persisted in the local node's vault.
 * @param expectedTransactionId An optional, expected transaction ID which can be checked upon receiving the note.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class ReceiveTransactionNoteFlow(
    private val session: FlowSession,
    private val persist: Boolean = true,
    private val expectedTransactionId: SecureHash? = null,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Pair<Party, TransactionNote>>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(RECEIVING, ADDING)

        private object ADDING : Step("Adding note to transaction.")
        private object RECEIVING : Step("Receiving transaction note from counter-party.")
    }

    @Suspendable
    override fun call(): Pair<Party, TransactionNote> {
        currentStep(RECEIVING, additionalLogInfo = session.counterparty.toString())
        val transactionNote = session.receive<TransactionNote>().unwrap { it }

        if (expectedTransactionId != null && expectedTransactionId != transactionNote.transactionId) {
            with("Received an unexpected transaction ID from counter-party: ${session.counterparty}.") {
                logger.error(this)
                throw FlowException(this)
            }
        }

        if (persist) {
            currentStep(ADDING, additionalLogInfo = transactionNote.transactionId.toString())
            serviceHub.vaultService.addNoteToTransaction(transactionNote.transactionId, transactionNote.text)
        }

        return session.counterparty to transactionNote
    }

    /**
     * Represents a flow initiated by [SendTransactionNoteFlow.Initiator] and receives a transaction note.
     *
     * @param session The flow session with the counter-party who is sending the transaction note.
     */
    @InitiatedBy(SendTransactionNoteFlow.Initiator::class)
    class Receiver(private val session: FlowSession) : FlowLogic<Pair<Party, TransactionNote>>() {

        private companion object {
            object RECEIVING : Step("Receiving transaction note.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(RECEIVING)

        @Suspendable
        override fun call(): Pair<Party, TransactionNote> {
            currentStep(RECEIVING)
            return subFlow(ReceiveTransactionNoteFlow(session, progressTracker = RECEIVING.childProgressTracker()))
        }
    }
}
