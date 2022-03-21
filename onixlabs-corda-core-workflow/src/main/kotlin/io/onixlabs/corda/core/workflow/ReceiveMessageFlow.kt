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
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.core.utilities.unwrap

/**
 * Represents a sub-flow that receives a message from the specified counter-parties.
 *
 * @param T The underlying message type.
 * @param session The flow session with the counter-party who is sending the message.
 * @param requestAcknowledgement Specifies whether the counter-parties are required to acknowledge the message.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class ReceiveMessageFlow<T : Message<*>>(
    private val session: FlowSession,
    private val requestAcknowledgement: Boolean = false,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Pair<Party, T>>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(ReceivingMessageStep, SendingMessageAcknowledgementStep)

        private object ReceivingMessageStep : Step("Receiving message from counter-party.")
        private object SendingMessageAcknowledgementStep : Step("Sending message acknowledgement to counter-party.")
    }

    @Suspendable
    override fun call(): Pair<Party, T> {
        val message = receiveMessageFromCounterparty()
        sendMessageAcknowledgementToCounterparty(message)
        return session.counterparty to message
    }

    @Suspendable
    @Suppress("UNCHECKED_CAST")
    private fun receiveMessageFromCounterparty(): T {
        currentStep(ReceivingMessageStep, additionalLogInfo = session.counterparty.toString())
        return session.receive<Message<*>>().unwrap { it as T }
    }

    @Suspendable
    private fun sendMessageAcknowledgementToCounterparty(message: T) {
        if (requestAcknowledgement) {
            currentStep(SendingMessageAcknowledgementStep, additionalLogInfo = session.counterparty.toString())
            session.send(MessageAcknowledgement(message.id))
        }
    }

    /**
     * Represents a flow initiated by [SendMessageFlow.Initiator] and receives a message.
     *
     * @param T The underlying message type.
     * @param session The flow session with the counter-party who is sending the message.
     */
    @InitiatedBy(SendMessageFlow.Initiator::class)
    class Receiver<T : Message<*>>(private val session: FlowSession) : FlowLogic<Pair<Party, T>>() {

        private companion object {
            object ReceivingMessageStep : Step("Receiving message.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(ReceivingMessageStep)

        @Suspendable
        override fun call(): Pair<Party, T> {
            currentStep(ReceivingMessageStep)
            return subFlow(ReceiveMessageFlow(session, false, ReceivingMessageStep.childProgressTracker()))
        }
    }
}
