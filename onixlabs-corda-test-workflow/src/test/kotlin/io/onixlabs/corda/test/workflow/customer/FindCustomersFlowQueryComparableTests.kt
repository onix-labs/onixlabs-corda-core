/**
 * Copyright 2020-2021 Matthew Layton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.onixlabs.corda.test.workflow.customer

import io.onixlabs.corda.core.query.*
import io.onixlabs.corda.test.contract.Customer
import io.onixlabs.corda.test.workflow.FindCustomersFlow
import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.IssueCustomerFlow
import io.onixlabs.corda.test.workflow.Pipeline
import net.corda.core.transactions.SignedTransaction
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindCustomersFlowQueryComparableTests : FlowTest() {

    override fun initialize() {
        Pipeline
            .create(network)
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_1, observers = setOf(partyB)) }
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_2, observers = setOf(partyB)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_3, observers = setOf(partyA)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_4, observers = setOf(partyA)) }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers comparableEqualTo birthday`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(birthday = comparableEqualTo(CUSTOMER_1_BIRTHDAY))
                }
                .finally {
                    assertEquals(1, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers comparableNotEqualTo birthday`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(birthday = comparableNotEqualTo(CUSTOMER_1_BIRTHDAY))
                }
                .finally {
                    assertEquals(3, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_4 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers greaterThan birthday`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(birthday = greaterThan(CUSTOMER_2_BIRTHDAY))
                }
                .finally {
                    assertEquals(2, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_4 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers greaterThanOrEqualTo birthday`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(birthday = greaterThanOrEqualTo(CUSTOMER_2_BIRTHDAY))
                }
                .finally {
                    assertEquals(3, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_4 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers lessThan birthday`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(birthday = lessThan(CUSTOMER_3_BIRTHDAY))
                }
                .finally {
                    assertEquals(2, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers lessThanOrEqualTo birthday`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(birthday = lessThanOrEqualTo(CUSTOMER_3_BIRTHDAY))
                }
                .finally {
                    assertEquals(3, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                }
        }
    }
}
