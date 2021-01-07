package io.onixlabs.corda.core.workflow.messaging

import io.onixlabs.corda.core.workflow.*
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
