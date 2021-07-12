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
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step

/**
 * Represents a sub-flow that sends a transaction note to the specified counter-parties.
 *
 * @param transactionNote The transaction note to send to the specified counter-parties.
 * @param sessions The sessions for each counter-party who should receive the transaction note.
 * @param addNoteToTransaction Determines whether the transaction note should be persisted in the local node's vault.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class SendTransactionNoteFlow(
    private val transactionNote: TransactionNote,
    private val sessions: Collection<FlowSession>,
    private val addNoteToTransaction: Boolean = true,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Unit>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(AddingTransactionNoteStep, SendingTransactionNoteStep)

        private const val FLOW_VERSION_1 = 1

        private object AddingTransactionNoteStep : Step("Adding note to transaction.")
        private object SendingTransactionNoteStep : Step("Sending transaction note to counter-parties.")
    }

    @Suspendable
    override fun call() {
        addTransactionNoteToVault()
        sendTransactionNoteToCounterparties()
    }

    @Suspendable
    private fun addTransactionNoteToVault() {
        if (addNoteToTransaction) {
            currentStep(AddingTransactionNoteStep, additionalLogInfo = transactionNote.transactionId.toString())
            serviceHub.vaultService.addNoteToTransaction(transactionNote.transactionId, transactionNote.text)
        }
    }

    @Suspendable
    private fun sendTransactionNoteToCounterparties() {
        currentStep(SendingTransactionNoteStep, false)
        sessions.forEach(::sendTransactionNoteToCounterparty)
    }

    @Suspendable
    private fun sendTransactionNoteToCounterparty(session: FlowSession) {
        logger.info("Sending transaction note to counter-party: ${session.counterparty}.")
        session.send(transactionNote)
    }

    /**
     * Represents an initiating flow that sends a transaction note to the specified counter-parties.
     *
     * @param transactionNote The transaction note to send to the specified counter-parties.
     * @param counterparties The counter-parties who should receive the transaction note.
     * @param addNoteToTransaction Determines whether the transaction note should be persisted in the local node's vault.
     */
    @StartableByRPC
    @StartableByService
    @InitiatingFlow(version = FLOW_VERSION_1)
    class Initiator(
        private val transactionNote: TransactionNote,
        private val counterparties: Iterable<Party>,
        private val addNoteToTransaction: Boolean = true
    ) : FlowLogic<Unit>() {

        private companion object {
            object SendingTransactionNoteStep : Step("Sending transaction note.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(SendingTransactionNoteStep)

        @Suspendable
        override fun call() {
            currentStep(SendingTransactionNoteStep)
            val sessions = initiateFlows(counterparties)
            subFlow(
                SendTransactionNoteFlow(
                    transactionNote,
                    sessions,
                    addNoteToTransaction,
                    SendingTransactionNoteStep.childProgressTracker()
                )
            )
        }
    }
}
