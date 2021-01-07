package io.onixlabs.corda.core.workflow.messaging

import io.onixlabs.corda.core.workflow.*
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
