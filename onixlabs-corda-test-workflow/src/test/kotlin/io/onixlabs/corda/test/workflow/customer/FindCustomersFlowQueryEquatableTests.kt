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

import io.onixlabs.corda.core.query.equatableEqualTo
import io.onixlabs.corda.core.query.equatableNotEqualTo
import io.onixlabs.corda.test.workflow.FindCustomersFlow
import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.IssueCustomerFlow
import io.onixlabs.corda.test.workflow.Pipeline
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindCustomersFlowQueryEquatableTests : FlowTest() {

    override fun initialize() {
        Pipeline
            .create(network)
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_1, observers = setOf(partyB)) }
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_2, observers = setOf(partyB)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_3, observers = setOf(partyA)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_4, observers = setOf(partyA)) }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers equatableEqualTo owner`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(owner = equatableEqualTo(partyA))
                }
                .finally {
                    assertEquals(2, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers equatableNotEqualTo owner`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(owner = equatableNotEqualTo(partyA))
                }
                .finally {
                    assertEquals(2, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_4 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers equatableEqualTo previousStateRef`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(previousStateRef = equatableEqualTo(null))
                }
                .finally {
                    assertEquals(4, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_4 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers equatableNotEqualTo previousStateRef`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(previousStateRef = equatableNotEqualTo(null))
                }
                .finally {
                    assertEquals(0, it.count())
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers equatableEqualTo linearId`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(linearId = equatableEqualTo(CUSTOMER_1_ID))
                }
                .finally {
                    assertEquals(1, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers equatableNotEqualTo linearId`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(linearId = equatableNotEqualTo(CUSTOMER_1_ID))
                }
                .finally {
                    assertEquals(3, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_4 })
                }
        }
    }
}
