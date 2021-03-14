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

import io.onixlabs.corda.test.workflow.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetCustomerRewardBalanceFlowTests : FlowTest() {

    override fun initialize() {
        Pipeline
            .create(network)
            .run(nodeA) { IssueCustomerFlow.Initiator(CUSTOMER_1, observers = setOf(partyC)) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_A1) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_A2) }
            .run(nodeC) { IssueRewardFlow.Initiator(REWARD_A3) }
    }

    @Test
    fun `GetCustomerRewardBalanceFlow should produce the correct sum of all rewards`() {
        Pipeline
            .create(network)
            .run(nodeA) {
                GetCustomerRewardBalanceFlow(CUSTOMER_1)
            }
            .finally {
                assertEquals(600, it)
            }
    }
}
