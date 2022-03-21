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
 * Represents a sub-flow that receives a message acknowledgement from the specified counter-party.
 *
 * @param session The flow session with the counter-party who is sending the message acknowledgement.
 * @property progressTracker The progress tracker that will be used to track the progress of communication in this flow.
 */
class ReceiveMessageAcknowledgementFlow<T : MessageAcknowledgement>(
    private val session: FlowSession,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Pair<Party, T>>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(ReceivingMessageAcknowledgementStep)

        private object ReceivingMessageAcknowledgementStep :
            Step("Receiving message acknowledgement from counter-party.")
    }

    @Suspendable
    @Suppress("UNCHECKED_CAST")
    override fun call(): Pair<Party, T> {
        currentStep(ReceivingMessageAcknowledgementStep, additionalLogInfo = session.counterparty.toString())
        return session.counterparty to session.receive<MessageAcknowledgement>().unwrap { it as T }
    }

    /**
     * Represents a flow initiated by [SendMessageAcknowledgementFlow.Initiator] and receives a message acknowledgement.
     *
     * @param session The flow session with the counter-party who is sending the message acknowledgement.
     */
    @InitiatedBy(SendMessageAcknowledgementFlow.Initiator::class)
    class Receiver<T : MessageAcknowledgement>(private val session: FlowSession) : FlowLogic<Pair<Party, T>>() {

        private companion object {
            object ReceivingMessageAcknowledgementStep : Step("Receiving message acknowledgement.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(ReceivingMessageAcknowledgementStep)

        @Suspendable
        override fun call(): Pair<Party, T> {
            currentStep(ReceivingMessageAcknowledgementStep)
            return subFlow(
                ReceiveMessageAcknowledgementFlow(
                    session,
                    ReceivingMessageAcknowledgementStep.childProgressTracker()
                )
            )
        }
    }
}
