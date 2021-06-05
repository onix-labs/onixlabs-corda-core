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

package io.onixlabs.corda.test.workflow.messaging

import io.onixlabs.corda.core.workflow.Message
import io.onixlabs.corda.core.workflow.ReceiveMessageFlow
import io.onixlabs.corda.core.workflow.SendMessageFlow
import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.Pipeline
import net.corda.core.concurrent.CordaFuture
import net.corda.core.identity.Party
import net.corda.core.toFuture
import net.corda.core.utilities.getOrThrow
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MessageFlowTests : FlowTest() {

    private lateinit var nodeBFuture: CordaFuture<ReceiveMessageFlow.Receiver<*>>
    private lateinit var nodeCFuture: CordaFuture<ReceiveMessageFlow.Receiver<*>>

    override fun initialize() {

        nodeBFuture = nodeB.registerInitiatedFlow(ReceiveMessageFlow.Receiver::class.java).toFuture()
        nodeCFuture = nodeC.registerInitiatedFlow(ReceiveMessageFlow.Receiver::class.java).toFuture()

        Pipeline
            .create(network)
            .run(nodeA) { SendMessageFlow.Initiator(Message("Hello, World!"), setOf(partyB, partyC)) }
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `Counterparty should receive expected message`() {
        listOf(nodeBFuture, nodeCFuture).forEach {
            val (sender, message) = it
                .getOrThrow()
                .stateMachine
                .resultFuture
                .getOrThrow() as Pair<Party, Message<String>>

            assertEquals(partyA, sender)
            assertEquals("Hello, World!", message.data)
        }
    }
}
