package io.onixlabs.corda.core.workflow.messaging

import io.onixlabs.corda.core.workflow.FlowTest
import io.onixlabs.corda.core.workflow.Message
import io.onixlabs.corda.core.workflow.MessageAcknowledgement
import io.onixlabs.corda.core.workflow.Pipeline
import io.onixlabs.corda.core.workflow.messaging.flows.SendFlow
import net.corda.core.identity.Party
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MessageWithAcknowledgementFlowTests : FlowTest() {

    private val message = Message("Hello, World!")
    private lateinit var result: Map<Party, MessageAcknowledgement>

    override fun initialize() {

        Pipeline
            .create(network)
            .run(nodeA) { SendFlow(message, setOf(partyB, partyC)) }
            .finally { result = it }
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `Sender should receive expected message acknowledgement`() {
        assertEquals(result.keys, setOf(partyB, partyC))
        result.forEach { (_, acknowledgement) -> assertEquals(message.id, acknowledgement.id) }
    }
}
