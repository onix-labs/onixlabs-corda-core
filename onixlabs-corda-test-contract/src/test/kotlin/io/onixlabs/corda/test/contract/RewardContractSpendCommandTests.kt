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

import io.onixlabs.corda.core.contract.SignatureData
import io.onixlabs.corda.test.*
import net.corda.core.contracts.hash
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test

class RewardContractSpendCommandTests : ContractTest() {

    @Test
    fun `On reward spending, the transaction must include the Spend command`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_A.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                fails()
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Spend(signature))
                verifies()
            }
        }
    }

    @Test
    fun `On reward spending, only one reward must be consumed`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_A.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                input(RewardContract.ID, REWARD_A)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Spend(signature))
                failsWith(RewardContract.Spend.CONTRACT_RULE_INPUTS)
            }
        }
    }

    @Test
    fun `On reward spending, zero reward states must be created`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_A.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                output(RewardContract.ID, REWARD_A)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Spend(signature))
                failsWith(RewardContract.Spend.CONTRACT_RULE_OUTPUTS)
            }
        }
    }

    @Test
    fun `On reward spending, only one customer state must be referenced`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_A.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                reference(CustomerContract.ID, CUSTOMER_B)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Spend(signature))
                failsWith(RewardContract.Spend.CONTRACT_RULE_REFERENCES)
            }
        }
    }

    @Test
    fun `On reward spending, the consumed reward's owner must be equal to the referenced customer's owner`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_A.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_B)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Spend(signature))
                failsWith(RewardContract.Spend.CONTRACT_RULE_OWNER)
            }
        }
    }

    @Test
    fun `On reward spending, the signature data must be equal to the hash of the consumed reward state`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_B.hash().bytes, IDENTITY_A.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Spend(signature))
                failsWith(RewardContract.Spend.CONTRACT_RULE_SIGNATURE_DATA)
            }
        }
    }

    @Test
    fun `On reward spending, the signature data must be signed by the owner of the reward state`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_C.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Spend(signature))
                failsWith(RewardContract.Spend.CONTRACT_RULE_SIGNATURE_SIGNER)
            }
        }
    }

    @Test
    fun `On reward spending, all participants of the consumed reward state must sign the transaction (IDENTITY_A missing)`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_A.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_C), RewardContract.Spend(signature))
                failsWith(RewardContract.Spend.CONTRACT_RULE_SIGNERS)
            }
        }
    }

    @Test
    fun `On reward spending, all participants of the consumed reward state must sign the transaction (IDENTITY_C missing)`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_A.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_A), RewardContract.Spend(signature))
                failsWith(RewardContract.Spend.CONTRACT_RULE_SIGNERS)
            }
        }
    }
}
