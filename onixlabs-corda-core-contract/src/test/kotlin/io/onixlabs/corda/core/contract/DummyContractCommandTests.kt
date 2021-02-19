package io.onixlabs.corda.core.contract

import net.corda.core.crypto.SecureHash
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test

class DummyContractCommandTests : ContractTest() {

    @Test
    fun `On dummy command, the dummy state must be signed by the state participant`() {
        services.ledger {
            transaction {
                val state = DummyContract.DummyState(listOf(IDENTITY_A.party))
                val content = SecureHash.randomSHA256().bytes
                val signature = SignatureData.create(content, IDENTITY_B.keyPair.private)
                output(DummyContract.ID, state)
                command(keysOf(IDENTITY_A), DummyContract.DummyCommand(signature))
                failsWith(DummyContract.DummyCommand.CONTRACT_RULE_COMMAND_SIGNED)
            }
        }
    }
}
