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

import io.onixlabs.corda.core.query.like
import io.onixlabs.corda.core.query.notLike
import io.onixlabs.corda.core.query.stringEqualTo
import io.onixlabs.corda.core.query.stringNotEqualTo
import io.onixlabs.corda.test.workflow.FindCustomersFlow
import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.IssueCustomerFlow
import io.onixlabs.corda.test.workflow.Pipeline
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindCustomersFlowQueryStringTests : FlowTest() {

    override fun initialize() {
        Pipeline
            .create(network)
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_1, observers = setOf(partyB)) }
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_2, observers = setOf(partyB)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_3, observers = setOf(partyA)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_4, observers = setOf(partyA)) }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers stringEqualTo firstName`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(firstName = stringEqualTo("John"))
                }
                .finally {
                    assertEquals(1, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers stringNotEqualTo firstName`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(firstName = stringNotEqualTo("John"))
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
    fun `FindCustomersFlow should find all matching customers like firstName`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(firstName = like("Ja%"))
                }
                .finally {
                    assertEquals(2, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers notLike firstName`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(firstName = notLike("Ja%"))
                }
                .finally {
                    assertEquals(2, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_4 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers stringEqualTo externalId`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(externalId = stringEqualTo(CUSTOMER_1_ID.externalId))
                }
                .finally {
                    assertEquals(1, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers stringEqualTo externalId (null)`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(externalId = stringEqualTo(null))
                }
                .finally {
                    assertEquals(2, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_3 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_4 })
                }
        }
    }

    @Test
    fun `FindCustomersFlow should find all matching customers stringNotEqualTo externalId`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(externalId = stringNotEqualTo(CUSTOMER_1_ID.externalId))
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
    fun `FindCustomersFlow should find all matching customers stringNotEqualTo externalId (null)`() {
        listOf(nodeA, nodeB).forEach {
            Pipeline
                .create(network)
                .run(it) {
                    FindCustomersFlow(externalId = stringNotEqualTo(null))
                }
                .finally {
                    assertEquals(2, it.count())
                    assertEquals(1, it.count { it.state.data == CUSTOMER_1 })
                    assertEquals(1, it.count { it.state.data == CUSTOMER_2 })
                }
        }
    }
}
