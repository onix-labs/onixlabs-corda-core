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
        fun tracker() = ProgressTracker(ADDING, SENDING)

        private const val FLOW_VERSION_1 = 1

        private object ADDING : Step("Adding note to transaction.")
        private object SENDING : Step("Sending transaction note to counter-parties.")
    }

    @Suspendable
    override fun call() {
        if (addNoteToTransaction) {
            currentStep(ADDING, additionalLogInfo = transactionNote.transactionId.toString())
            serviceHub.vaultService.addNoteToTransaction(transactionNote.transactionId, transactionNote.text)
        }

        currentStep(SENDING, false)
        sessions.forEach {
            logger.info("Sending transaction note to counter-party: ${it.counterparty}.")
            it.send(transactionNote)
        }
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
            object SENDING : Step("Sending transaction note.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(SENDING)

        @Suspendable
        override fun call() {
            currentStep(SENDING)
            val sessions = initiateFlows(counterparties)
            subFlow(
                SendTransactionNoteFlow(
                    transactionNote,
                    sessions,
                    addNoteToTransaction,
                    SENDING.childProgressTracker()
                )
            )
        }
    }
}
