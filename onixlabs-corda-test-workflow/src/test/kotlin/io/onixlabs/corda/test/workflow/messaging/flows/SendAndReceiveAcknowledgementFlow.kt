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

package io.onixlabs.corda.test.workflow.messaging.flows

import co.paralleluniverse.fibers.Suspendable
import io.onixlabs.corda.core.workflow.*
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.identity.Party

@InitiatingFlow
class SendAndReceiveAcknowledgementFlow<T>(
    private val message: T,
    private val counterparties: Set<Party>
) : FlowLogic<Map<Party, MessageAcknowledgement>>() where T : Message<*> {

    @Suspendable
    override fun call(): Map<Party, MessageAcknowledgement> {
        val sessions = initiateFlows(counterparties)
        return subFlow(SendMessageFlow(message, sessions, true))
    }

    @InitiatedBy(SendAndReceiveAcknowledgementFlow::class)
    private class Handler<T>(private val session: FlowSession) : FlowLogic<Pair<Party, T>>() where T : Message<*> {

        @Suspendable
        override fun call(): Pair<Party, T> {
            return subFlow(ReceiveMessageFlow(session, true))
        }
    }
}
