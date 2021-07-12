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
 * @param addTransactionNoteToVault Determines whether the transaction note should be added in the local node's vault.
 * @param expectedTransactionId An optional, expected transaction ID which can be checked upon receiving the note.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class ReceiveTransactionNoteFlow(
    private val session: FlowSession,
    private val addTransactionNoteToVault: Boolean = true,
    private val expectedTransactionId: SecureHash? = null,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Pair<Party, TransactionNote>>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(ReceivingTransactionNoteStep, AddingTransactionNoteStep)

        private object AddingTransactionNoteStep : Step("Adding note to transaction.")
        private object ReceivingTransactionNoteStep : Step("Receiving transaction note from counter-party.")
    }

    @Suspendable
    override fun call(): Pair<Party, TransactionNote> {
        val transactionNote = receiveTransactionNoteFromCounterparty()
        checkExpectedTransactionNote(transactionNote)
        addTransactionNoteToVault(transactionNote)
        return session.counterparty to transactionNote
    }

    @Suspendable
    private fun receiveTransactionNoteFromCounterparty(): TransactionNote {
        currentStep(ReceivingTransactionNoteStep, additionalLogInfo = session.counterparty.toString())
        return session.receive<TransactionNote>().unwrap { it }
    }

    @Suspendable
    private fun checkExpectedTransactionNote(transactionNote: TransactionNote) {
        if (expectedTransactionId != null && expectedTransactionId != transactionNote.transactionId) {
            throw FlowException("Received an unexpected transaction ID from counter-party: ${session.counterparty}.")
        }
    }

    @Suspendable
    private fun addTransactionNoteToVault(transactionNote: TransactionNote) {
        if (addTransactionNoteToVault) {
            currentStep(AddingTransactionNoteStep, additionalLogInfo = transactionNote.transactionId.toString())
            serviceHub.vaultService.addNoteToTransaction(transactionNote.transactionId, transactionNote.text)
        }
    }

    /**
     * Represents a flow initiated by [SendTransactionNoteFlow.Initiator] and receives a transaction note.
     *
     * @param session The flow session with the counter-party who is sending the transaction note.
     */
    @InitiatedBy(SendTransactionNoteFlow.Initiator::class)
    class Receiver(private val session: FlowSession) : FlowLogic<Pair<Party, TransactionNote>>() {

        private companion object {
            object ReceivingTransactionNoteStep : Step("Receiving transaction note.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(ReceivingTransactionNoteStep)

        @Suspendable
        override fun call(): Pair<Party, TransactionNote> {
            currentStep(ReceivingTransactionNoteStep)
            return subFlow(
                ReceiveTransactionNoteFlow(
                    session,
                    progressTracker = ReceivingTransactionNoteStep.childProgressTracker()
                )
            )
        }
    }
}
