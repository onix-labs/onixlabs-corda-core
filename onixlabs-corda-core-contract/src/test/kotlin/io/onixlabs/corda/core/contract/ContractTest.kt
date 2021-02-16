package io.onixlabs.corda.core.contract

import net.corda.core.node.NotaryInfo
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import org.junit.jupiter.api.BeforeEach

abstract class ContractTest {

    protected companion object {
        private val cordapps = listOf("io.onixlabs.corda.core.contract")
        private val contracts = listOf(DummyContract.ID)

        fun keysOf(vararg identities: TestIdentity) = identities.map { it.publicKey }
    }

    private lateinit var _services: MockServices
    protected val services: MockServices get() = _services

    @BeforeEach
    private fun setup() {
        val networkParameters = testNetworkParameters(
            minimumPlatformVersion = 8,
            notaries = listOf(NotaryInfo(NOTARY.party, true))
        )
        _services = MockServices(cordapps, IDENTITY_A, networkParameters, IDENTITY_B, IDENTITY_C)
        contracts.forEach { _services.addMockCordapp(it) }
    }
}
