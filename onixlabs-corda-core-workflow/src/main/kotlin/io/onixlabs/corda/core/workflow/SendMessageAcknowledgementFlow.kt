/*
 * Copyright 2020-2022 ONIXLabs
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
 * Represents a sub-flow that sends a message acknowledgement to the specified counter-party.
 *
 * @param acknowledgement The message acknowledgement to send to the counter-party.
 * @param session The session with the counter-party who will receive the message acknowledgement.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class SendMessageAcknowledgementFlow<T : MessageAcknowledgement>(
    private val acknowledgement: T,
    private val session: FlowSession,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Unit>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(SendingMessageAcknowledgementStep)

        private const val FLOW_VERSION_1 = 1

        private object SendingMessageAcknowledgementStep : Step("Sending message acknowledgement to counter-party.")
    }

    @Suspendable
    override fun call() {
        currentStep(SendingMessageAcknowledgementStep, additionalLogInfo = session.counterparty.toString())
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
    class Initiator<T : MessageAcknowledgement>(
        private val acknowledgement: T,
        private val counterparty: Party
    ) : FlowLogic<Unit>() {

        private companion object {
            private object SendingMessageAcknowledgementStep : Step("Sending message acknowledgement.") {
                override fun childProgressTracker(): ProgressTracker = tracker()
            }
        }

        override val progressTracker = ProgressTracker(SendingMessageAcknowledgementStep)

        @Suspendable
        override fun call() {
            currentStep(SendingMessageAcknowledgementStep)
            val session = initiateFlow(counterparty)
            subFlow(
                SendMessageAcknowledgementFlow(
                    acknowledgement,
                    session,
                    SendingMessageAcknowledgementStep.childProgressTracker()
                )
            )
        }
    }
}
