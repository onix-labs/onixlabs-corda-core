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

package io.onixlabs.corda.test.workflow.customer

import io.onixlabs.corda.core.services.*
import io.onixlabs.corda.test.contract.Customer
import io.onixlabs.corda.test.contract.CustomerEntity
import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.IssueCustomerFlow
import io.onixlabs.corda.test.workflow.Pipeline
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CustomerVaultServiceTests : FlowTest() {

    override fun initialize() {
        Pipeline
            .create(network)
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_1, observers = setOf(partyB)) }
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_2, observers = setOf(partyB)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_3, observers = setOf(partyA)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_4, observers = setOf(partyA)) }
    }

    @Test
    fun `VaultService linearIds should find all matching customers by linearId`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                linearIds(CUSTOMER_1_ID)
            }.toList()

            assertEquals(1, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
        }
    }

    @Test
    fun `VaultService notEqualTo should find all matching customers by linearId`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::linearId notEqualTo CUSTOMER_1_ID.id)
            }.toList()

            assertEquals(3, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService equalTo should find all matching customers by owner`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::owner equalTo partyA)
            }.toList()

            assertEquals(2, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
        }
    }

    @Test
    fun `VaultService notEqualTo should find all matching customers by owner`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::owner notEqualTo partyA)
            }.toList()

            assertEquals(2, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService equalTo should find all matching customers by previousStateRef`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::previousStateRef equalTo null)
            }.toList()

            assertEquals(4, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService notEqualTo find all matching customers by previousStateRef`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::previousStateRef notEqualTo null)
            }.toList()

            assertEquals(0, results.count())
        }
    }

    @Test
    fun `VaultService equalTo should find all matching customers by firstName`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::firstName equalTo "John")
            }.toList()

            assertEquals(1, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
        }
    }

    @Test
    fun `VaultService notEqualTo should find all matching customers by firstName`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::firstName notEqualTo "John")
            }.toList()

            assertEquals(3, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService equalTo should find all matching customers by externalId`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::externalId equalTo CUSTOMER_1_ID.externalId)
            }.toList()

            assertEquals(1, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
        }
    }

    @Test
    fun `VaultService equalTo should find all matching customers by externalId (null)`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::externalId equalTo null)
            }.toList()

            assertEquals(2, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService notEqualTo should find all matching customers by externalId`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::externalId notEqualTo CUSTOMER_1_ID.externalId)
            }.toList()

            assertEquals(3, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService notEqualTo should find all matching customers by externalId (null)`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::externalId notEqualTo null)
            }.toList()

            assertEquals(2, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
        }
    }

    @Test
    fun `VaultService isNull should find all matching customers by previousStateRef`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::previousStateRef.isNull())
            }.toList()

            assertEquals(4, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService isNotNull find all matching customers by previousStateRef`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::previousStateRef.isNotNull())
            }.toList()

            assertEquals(0, results.count())
        }
    }

    @Test
    fun `VaultService like should find all matching customers by firstName`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::firstName like "Ja%")
            }.toList()

            assertEquals(2, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
        }
    }

    @Test
    fun `VaultService notLike should find all matching customers by firstName`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::firstName notLike "Ja%")
            }.toList()

            assertEquals(2, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService equalTo should find all matching customers by birthday`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::birthday equalTo CUSTOMER_1_BIRTHDAY)
            }.toList()

            assertEquals(1, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
        }
    }

    @Test
    fun `VaultService notEqualTo should find all matching customers by birthday`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::birthday notEqualTo CUSTOMER_1_BIRTHDAY)
            }.toList()

            assertEquals(3, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService greaterThan should find all matching customers by birthday`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::birthday greaterThan CUSTOMER_2_BIRTHDAY)
            }.toList()

            assertEquals(2, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService greaterThanOrEqualTo should find all matching customers by birthday`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::birthday greaterThanOrEqualTo CUSTOMER_2_BIRTHDAY)
            }.toList()

            assertEquals(3, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_4 })
        }
    }

    @Test
    fun `VaultService lessThan should find all matching customers by birthday`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::birthday lessThan CUSTOMER_3_BIRTHDAY)
            }.toList()

            assertEquals(2, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
        }
    }

    @Test
    fun `VaultService lessThanOtEqualTo should find all matching customers by birthday`() {
        listOf(nodeA, nodeB).forEach {

            val results = it.services.vaultServiceFor<Customer>().filter {
                where(CustomerEntity::birthday lessThanOrEqualTo CUSTOMER_3_BIRTHDAY)
            }.toList()

            assertEquals(3, results.count())
            assertEquals(1, results.count { it.state.data == CUSTOMER_1 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_2 })
            assertEquals(1, results.count { it.state.data == CUSTOMER_3 })
        }
    }
}
