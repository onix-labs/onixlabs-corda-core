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
class SendMessageFlow<T>(
    private val message: T,
    private val sessions: Set<FlowSession>,
    private val requestAcknowledgement: Boolean = false,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Map<Party, MessageAcknowledgement>>() where T : Message<*> {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(SENDING, RECEIVING)

        private const val FLOW_VERSION_1 = 1

        private object SENDING : Step("Sending message to counter-parties.")
        private object RECEIVING : Step("Receiving message acknowledgement from counter-party.")
    }

    @Suspendable
    override fun call(): Map<Party, MessageAcknowledgement> {
        return if (requestAcknowledgement) sendMessageAndReceiveAcknowledgement() else sendMessage()
    }

    @Suspendable
    private fun sendMessage(): Map<Party, MessageAcknowledgement> {
        currentStep(SENDING, false)
        sessions.forEach {
            logger.info("Sending message to counter-party: ${it.counterparty}.")
            it.send(message)
        }

        return emptyMap()
    }

    @Suspendable
    private fun sendMessageAndReceiveAcknowledgement(): Map<Party, MessageAcknowledgement> {
        currentStep(SENDING, false)
        return sessions.map {
            logger.info("Sending message to counter-party: ${it.counterparty}.")
            currentStep(RECEIVING, false)
            logger.info("Receiving message acknowledgement from counter-party: ${it.counterparty}.")
            val acknowledgement = it.sendAndReceive<MessageAcknowledgement>(message).unwrap { it }
            it.counterparty to acknowledgement
        }.toMap()
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
            object SENDING : Step("Sending message.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(SENDING)

        @Suspendable
        override fun call(): Map<Party, MessageAcknowledgement> {
            currentStep(SENDING)
            val sessions = initiateFlows(counterparties)
            return subFlow(SendMessageFlow(message, sessions, false, SENDING.childProgressTracker()))
        }
    }
}
