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

package io.onixlabs.corda.test.contract

import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.node.NotaryInfo
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.core.TestIdentity
import net.corda.testing.dsl.LedgerDSL
import net.corda.testing.dsl.TestLedgerDSLInterpreter
import net.corda.testing.dsl.TestTransactionDSLInterpreter
import net.corda.testing.node.MockServices
import org.junit.jupiter.api.BeforeEach

typealias MockLedger = LedgerDSL<TestTransactionDSLInterpreter, TestLedgerDSLInterpreter>

abstract class ContractTest {

    protected companion object {
        private val cordapps = listOf(
            "io.onixlabs.corda.core.contract",
            "io.onixlabs.corda.test.contract"
        )

        private val contracts = listOf(
            CustomerContract.ID,
            RewardContract.ID
        )

        fun keysOf(vararg identities: TestIdentity) = identities.map { it.publicKey }
    }

    private lateinit var _services: MockServices
    protected val services: MockServices get() = _services

    @BeforeEach
    private fun setup() {
        val networkParameters = testNetworkParameters(
            minimumPlatformVersion = 10,
            notaries = listOf(NotaryInfo(NOTARY.party, true))
        )
        _services = MockServices(cordapps, IDENTITY_A, networkParameters, IDENTITY_B, IDENTITY_C)
        contracts.forEach { _services.addMockCordapp(it) }
    }

    fun MockLedger.issue(customer: Customer): StateAndRef<Customer> {
        val label = SecureHash.randomSHA256().toString()

        transaction {
            output(CustomerContract.ID, label, customer)
            command(listOf(customer.owner.owningKey), CustomerContract.Issue)
            verifies()
        }

        return retrieveOutputStateAndRef(customer.javaClass, label)
    }

    fun StateAndRef<Customer>.evolve(ref: StateRef = this.ref): Customer {
        return state.data.copy(previousStateRef = ref)
    }
}
