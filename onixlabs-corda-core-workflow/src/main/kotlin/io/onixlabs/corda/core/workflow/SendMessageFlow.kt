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
import net.corda.core.utilities.unwrap

/**
 * Represents a sub-flow that sends a message to the specified counter-parties.
 *
 * @param T The underlying message type.
 * @param message The message to send to the specified counter-parties.
 * @param sessions The sessions for each counter-party who should receive the transaction note.
 * @param requestAcknowledgement Specifies whether the counter-parties are required to acknowledge the message.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class SendMessageFlow<T : Message<*>>(
    private val message: T,
    private val sessions: Set<FlowSession>,
    private val requestAcknowledgement: Boolean = false,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Map<Party, MessageAcknowledgement>>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(SendingMessageStep, ReceivingAcknowledgementStep)

        private const val FLOW_VERSION_1 = 1

        private object SendingMessageStep : Step("Sending message to counter-parties.")
        private object ReceivingAcknowledgementStep : Step("Receiving message acknowledgement from counter-party.")
    }

    @Suspendable
    override fun call(): Map<Party, MessageAcknowledgement> {
        return if (requestAcknowledgement) sendMessageAndReceiveAcknowledgement() else sendMessage()
    }

    @Suspendable
    private fun sendMessage(): Map<Party, MessageAcknowledgement> {
        currentStep(SendingMessageStep, false)
        sessions.forEach(::sendMessageToCounterparty)
        return emptyMap()
    }

    @Suspendable
    private fun sendMessageAndReceiveAcknowledgement(): Map<Party, MessageAcknowledgement> {
        currentStep(SendingMessageStep, false)
        return sessions.map {
            sendMessageToCounterparty(it)
            receiveAcknowledgementFromCounterparty(it)
        }.toMap()
    }

    @Suspendable
    private fun sendMessageToCounterparty(session: FlowSession) {
        logger.info("Sending message to counter-party: ${session.counterparty}")
        session.send(message)
    }

    @Suspendable
    private fun receiveAcknowledgementFromCounterparty(session: FlowSession): Pair<Party, MessageAcknowledgement> {
        logger.info("Receiving message acknowledgement from counter-party: ${session.counterparty}.")
        return subFlow(ReceiveMessageAcknowledgementFlow(session))
    }

    /**
     * Represents an initiating flow that sends a message to the specified counter-parties.
     *
     * @param T The underlying message type.
     * @param message The message to send to the specified counter-parties.
     * @param counterparties The counter-parties who should receive the message.
     */
    @StartableByRPC
    @StartableByService
    @InitiatingFlow(version = FLOW_VERSION_1)
    class Initiator<T>(
        private val message: T,
        private val counterparties: Collection<Party>
    ) : FlowLogic<Map<Party, MessageAcknowledgement>>() where T : Message<*> {

        private companion object {
            object SendingMessageStep : Step("Sending message.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(SendingMessageStep)

        @Suspendable
        override fun call(): Map<Party, MessageAcknowledgement> {
            currentStep(SendingMessageStep)
            val sessions = initiateFlows(counterparties)
            return subFlow(SendMessageFlow(message, sessions, false, SendingMessageStep.childProgressTracker()))
        }
    }
}
