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
 * Represents a sub-flow that sends a message acknowledgement to the specified counter-party.
 *
 * @param acknowledgement The message acknowledgement to send to the counter-party.
 * @param session The session with the counter-party who will receive the message acknowledgement.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class SendMessageAcknowledgementFlow<T>(
    private val acknowledgement: T,
    private val session: FlowSession,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Unit>() where T : MessageAcknowledgement {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(SENDING)

        private const val FLOW_VERSION_1 = 1

        private object SENDING : Step("Sending message acknowledgement to counter-party.")
    }

    @Suspendable
    override fun call() {
        currentStep(SENDING, additionalLogInfo = session.counterparty.toString())
        session.send(acknowledgement)
    }

    /**
     * Represents an initiating flow that sends a message acknowledgement to the specified counter-party.
     *
     * @param acknowledgement The message acknowledgement to send to the counter-party.
     * @param counterparty The counter-party who will receive the message acknowledgement.
     */
    @StartableByRPC
    @StartableByService
    @InitiatingFlow(version = FLOW_VERSION_1)
    class Initiator<T>(
        private val acknowledgement: T,
        private val counterparty: Party
    ) : FlowLogic<Unit>() where T : MessageAcknowledgement {

        private companion object {
            private object SENDING : Step("Sending message acknowledgement.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(SENDING)

        @Suspendable
        override fun call() {
            currentStep(SENDING)
            val session = initiateFlow(counterparty)
            subFlow(SendMessageAcknowledgementFlow(acknowledgement, session, SENDING.childProgressTracker()))
        }
    }
}
