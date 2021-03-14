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

package io.onixlabs.corda.test.workflow.reward

import io.onixlabs.corda.core.query.*
import io.onixlabs.corda.test.workflow.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindRewardsFlowQueryComparableTests : FlowTest() {

    override fun initialize() {
        Pipeline
            .create(network)
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_1, observers = setOf(partyC)) }
            .run(nodeB) { IssueCustomerFlow.Initiator(CUSTOMER_3, observers = setOf(partyC)) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_A1) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_A2) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_A3) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_B1) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_B2) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_B3) }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards comparableEqualTo points`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(points = comparableEqualTo(200))
            }
            .finally {
                assertEquals(2, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_A2 })
                assertEquals(1, it.count { it.state.data == REWARD_B2 })
            }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards comparableNotEqualTo points`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(points = comparableNotEqualTo(200))
            }
            .finally {
                assertEquals(4, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_A1 })
                assertEquals(1, it.count { it.state.data == REWARD_A3 })
                assertEquals(1, it.count { it.state.data == REWARD_B1 })
                assertEquals(1, it.count { it.state.data == REWARD_B3 })
            }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards greaterThan points`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(points = greaterThan(100))
            }
            .finally {
                assertEquals(4, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_A2 })
                assertEquals(1, it.count { it.state.data == REWARD_A3 })
                assertEquals(1, it.count { it.state.data == REWARD_B2 })
                assertEquals(1, it.count { it.state.data == REWARD_B3 })
            }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards greaterThanOrEqualTo points`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(points = greaterThanOrEqualTo(200))
            }
            .finally {
                assertEquals(4, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_A2 })
                assertEquals(1, it.count { it.state.data == REWARD_A3 })
                assertEquals(1, it.count { it.state.data == REWARD_B2 })
                assertEquals(1, it.count { it.state.data == REWARD_B3 })
            }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards lessThan points`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(points = lessThan(200))
            }
            .finally {
                assertEquals(2, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_A1 })
                assertEquals(1, it.count { it.state.data == REWARD_B1 })
            }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards lessThanOrEqualTo points`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(points = lessThanOrEqualTo(200))
            }
            .finally {
                assertEquals(4, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_A1 })
                assertEquals(1, it.count { it.state.data == REWARD_A2 })
                assertEquals(1, it.count { it.state.data == REWARD_B1 })
                assertEquals(1, it.count { it.state.data == REWARD_B2 })
            }
    }
}
