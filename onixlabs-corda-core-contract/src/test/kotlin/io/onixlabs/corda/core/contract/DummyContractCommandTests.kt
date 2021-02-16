package io.onixlabs.corda.core.contract

import net.corda.core.contracts.hash
import net.corda.core.crypto.sign
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test

class DummyContractCommandTests : ContractTest() {

    @Test
    fun `On dummy command, the dummy state must be signed by the state participant`() {
        services.ledger {
            transaction {
                val state = DummyContract.DummyState(listOf(IDENTITY_A.party))
                val signature = IDENTITY_B.keyPair.private.sign(state.hash().bytes)
                output(DummyContract.ID, state)
                command(keysOf(IDENTITY_A), DummyContract.DummyCommand(signature))
                failsWith(DummyContract.DummyCommand.CONTRACT_RULE_STATES_WERE_SIGNED)
            }
        }
    }
}
