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

package io.onixlabs.corda.test.workflow.messaging

import io.onixlabs.corda.core.workflow.MessageAcknowledgement
import io.onixlabs.corda.core.workflow.ReceiveMessageAcknowledgementFlow
import io.onixlabs.corda.core.workflow.SendMessageAcknowledgementFlow
import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.Pipeline
import net.corda.core.concurrent.CordaFuture
import net.corda.core.identity.Party
import net.corda.core.toFuture
import net.corda.core.utilities.getOrThrow
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class MessageAcknowledgementFlowTests : FlowTest() {

    private val acknowledgement = MessageAcknowledgement(UUID.fromString("e3aceb0c-b242-4768-8642-098aa983f120"))
    private lateinit var nodeBFuture: CordaFuture<ReceiveMessageAcknowledgementFlow.Receiver<*>>

    override fun initialize() {

        nodeBFuture = nodeB.registerInitiatedFlow(ReceiveMessageAcknowledgementFlow.Receiver::class.java).toFuture()

        Pipeline
            .create(network)
            .run(nodeA) { SendMessageAcknowledgementFlow.Initiator(acknowledgement, partyB) }
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `Counterparty should receive expected message acknowledgement`() {
        val (sender, acknowledgement) = nodeBFuture
            .getOrThrow()
            .stateMachine
            .resultFuture
            .getOrThrow() as Pair<Party, MessageAcknowledgement>

        assertEquals(partyA, sender)
        assertEquals("e3aceb0c-b242-4768-8642-098aa983f120", acknowledgement.id.toString())
    }
}
