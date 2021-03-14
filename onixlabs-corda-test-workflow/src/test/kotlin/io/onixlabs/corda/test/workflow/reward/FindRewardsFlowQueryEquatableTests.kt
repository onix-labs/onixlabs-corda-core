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

import io.onixlabs.corda.core.query.equatableEqualTo
import io.onixlabs.corda.core.query.equatableNotEqualTo
import io.onixlabs.corda.test.workflow.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindRewardsFlowQueryEquatableTests : FlowTest() {

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
    fun `FindRewardsFlow should find all matching rewards equatableEqualTo issuer`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(issuer = equatableEqualTo(partyC))
            }
            .finally {
                assertEquals(6, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_A1 })
                assertEquals(1, it.count { it.state.data == REWARD_A2 })
                assertEquals(1, it.count { it.state.data == REWARD_A3 })
                assertEquals(1, it.count { it.state.data == REWARD_B1 })
                assertEquals(1, it.count { it.state.data == REWARD_B2 })
                assertEquals(1, it.count { it.state.data == REWARD_B3 })
            }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards equatableNotEqualTo issuer`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(issuer = equatableNotEqualTo(partyC))
            }
            .finally {
                assertEquals(0, it.count())
            }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards equatableEqualTo owner`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(owner = equatableEqualTo(partyA))
            }
            .finally {
                assertEquals(3, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_A1 })
                assertEquals(1, it.count { it.state.data == REWARD_A2 })
                assertEquals(1, it.count { it.state.data == REWARD_A3 })
            }
    }

    @Test
    fun `FindRewardsFlow should find all matching rewards equatableNotEqualTo owner`() {
        Pipeline
            .create(network)
            .run(nodeC) {
                FindRewardsFlow(owner = equatableNotEqualTo(partyA))
            }
            .finally {
                assertEquals(3, it.count())
                assertEquals(1, it.count { it.state.data == REWARD_B1 })
                assertEquals(1, it.count { it.state.data == REWARD_B2 })
                assertEquals(1, it.count { it.state.data == REWARD_B3 })
            }
    }
}
