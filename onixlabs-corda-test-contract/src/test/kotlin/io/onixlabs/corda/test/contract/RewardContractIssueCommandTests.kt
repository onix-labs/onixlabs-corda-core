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

package io.onixlabs.corda.test.contract

import io.onixlabs.corda.core.contract.SignatureData
import net.corda.core.contracts.hash
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test

class RewardContractIssueCommandTests : ContractTest() {

    @Test
    fun `On reward issuing, the transaction must include the Issue command`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_C.keyPair.private)
                output(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                fails()
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Issue(signature))
                verifies()
            }
        }
    }

    @Test
    fun `On reward issuing, zero reward states must be consumed`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_C.keyPair.private)
                input(RewardContract.ID, REWARD_A)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Issue(signature))
                failsWith(RewardContract.Issue.CONTRACT_RULE_INPUTS)
            }
        }
    }

    @Test
    fun `On reward issuing, only one reward state must be created`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_C.keyPair.private)
                output(RewardContract.ID, REWARD_A)
                output(RewardContract.ID, REWARD_A)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Issue(signature))
                failsWith(RewardContract.Issue.CONTRACT_RULE_OUTPUTS)
            }
        }
    }

    @Test
    fun `On reward issuing, only one customer state must be referenced`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_C.keyPair.private)
                output(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                reference(CustomerContract.ID, CUSTOMER_B)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Issue(signature))
                failsWith(RewardContract.Issue.CONTRACT_RULE_REFERENCES)
            }
        }
    }

    @Test
    fun `On reward issuing, the created reward's owner must be equal to the referenced customer's owner`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_C.keyPair.private)
                output(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_B)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Issue(signature))
                failsWith(RewardContract.Issue.CONTRACT_RULE_OWNER)
            }
        }
    }

    @Test
    fun `On reward issuing, the signature data must be equal to the hash of the created reward state`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_B.hash().bytes, IDENTITY_C.keyPair.private)
                output(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Issue(signature))
                failsWith(RewardContract.Issue.CONTRACT_RULE_SIGNATURE_DATA)
            }
        }
    }

    @Test
    fun `On reward issuing, the signature data must be signed by the issuer of the created reward state`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_A.keyPair.private)
                output(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_A, IDENTITY_C), RewardContract.Issue(signature))
                failsWith(RewardContract.Issue.CONTRACT_RULE_SIGNATURE_SIGNER)
            }
        }
    }

    @Test
    fun `On reward issuing, all participants of the created reward state must sign the transaction (IDENTITY_A missing)`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_C.keyPair.private)
                output(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_C), RewardContract.Issue(signature))
                failsWith(RewardContract.Issue.CONTRACT_RULE_SIGNERS)
            }
        }
    }

    @Test
    fun `On reward issuing, all participants of the created reward state must sign the transaction (IDENTITY_C missing)`() {
        services.ledger {
            transaction {
                val signature = SignatureData.create(REWARD_A.hash().bytes, IDENTITY_C.keyPair.private)
                output(RewardContract.ID, REWARD_A)
                reference(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_A), RewardContract.Issue(signature))
                failsWith(RewardContract.Issue.CONTRACT_RULE_SIGNERS)
            }
        }
    }
}
