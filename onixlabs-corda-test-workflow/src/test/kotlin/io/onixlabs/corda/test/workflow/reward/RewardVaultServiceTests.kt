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

package io.onixlabs.corda.test.workflow.reward

import io.onixlabs.corda.core.services.*
import io.onixlabs.corda.test.contract.Reward
import io.onixlabs.corda.test.contract.RewardEntity
import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.IssueCustomerFlow
import io.onixlabs.corda.test.workflow.IssueRewardFlow
import io.onixlabs.corda.test.workflow.Pipeline
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RewardVaultServiceTests : FlowTest() {

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
    fun `VaultService equalTo should find all matching rewards by issuer`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::issuer equalTo partyC)
        }.toList()

        assertEquals(6, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_A1 })
        assertEquals(1, results.count { it.state.data == REWARD_A2 })
        assertEquals(1, results.count { it.state.data == REWARD_A3 })
        assertEquals(1, results.count { it.state.data == REWARD_B1 })
        assertEquals(1, results.count { it.state.data == REWARD_B2 })
        assertEquals(1, results.count { it.state.data == REWARD_B3 })
    }

    @Test
    fun `VaultService notEqualTo should find all matching rewards by issuer`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::issuer notEqualTo partyC)
        }.toList()

        assertEquals(0, results.count())
    }

    @Test
    fun `VaultService equalTo should find all matching rewards by owner`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::owner equalTo partyA)
        }.toList()

        assertEquals(3, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_A1 })
        assertEquals(1, results.count { it.state.data == REWARD_A2 })
        assertEquals(1, results.count { it.state.data == REWARD_A3 })
    }

    @Test
    fun `VaultService notEqualTo should find all matching rewards by owner`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::owner notEqualTo partyA)
        }.toList()

        assertEquals(3, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_B1 })
        assertEquals(1, results.count { it.state.data == REWARD_B2 })
        assertEquals(1, results.count { it.state.data == REWARD_B3 })
    }

    @Test
    fun `VaultService equalTo should find all matching rewards by points`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::points equalTo 200)
        }.toList()

        assertEquals(2, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_A2 })
        assertEquals(1, results.count { it.state.data == REWARD_B2 })
    }

    @Test
    fun `VaultService notEqualTo should find all matching rewards by points`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::points notEqualTo 200)
        }.toList()

        assertEquals(4, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_A1 })
        assertEquals(1, results.count { it.state.data == REWARD_A3 })
        assertEquals(1, results.count { it.state.data == REWARD_B1 })
        assertEquals(1, results.count { it.state.data == REWARD_B3 })
    }

    @Test
    fun `VaultService greaterThan should find all matching rewards by points`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::points greaterThan 100)
        }.toList()

        assertEquals(4, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_A2 })
        assertEquals(1, results.count { it.state.data == REWARD_A3 })
        assertEquals(1, results.count { it.state.data == REWARD_B2 })
        assertEquals(1, results.count { it.state.data == REWARD_B3 })
    }

    @Test
    fun `VaultService greaterThanOrEqualTo should find all matching rewards by points`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::points greaterThanOrEqualTo 200)
        }.toList()

        assertEquals(4, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_A2 })
        assertEquals(1, results.count { it.state.data == REWARD_A3 })
        assertEquals(1, results.count { it.state.data == REWARD_B2 })
        assertEquals(1, results.count { it.state.data == REWARD_B3 })
    }

    @Test
    fun `VaultService lessThan should find all matching rewards by points`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::points lessThan 200)
        }.toList()

        assertEquals(2, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_A1 })
        assertEquals(1, results.count { it.state.data == REWARD_B1 })
    }

    @Test
    fun `VaultService lessThanOrEqualTo should find all matching rewards by points`() {

        val results = nodeC.services.vaultServiceFor<Reward>().filter {
            expression(RewardEntity::points lessThanOrEqualTo 200)
        }.toList()

        assertEquals(4, results.count())
        assertEquals(1, results.count { it.state.data == REWARD_A1 })
        assertEquals(1, results.count { it.state.data == REWARD_A2 })
        assertEquals(1, results.count { it.state.data == REWARD_B1 })
        assertEquals(1, results.count { it.state.data == REWARD_B2 })
    }
}
