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

import io.onixlabs.corda.core.contract.*
import net.corda.core.contracts.Contract
import net.corda.core.contracts.hash
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

class RewardContract : Contract {

    companion object : ContractID

    override fun verify(tx: LedgerTransaction) = tx.allowCommands(Issue::class.java, Spend::class.java)

    interface RewardContractCommand : VerifiedCommandData

    class Issue(override val signature: SignatureData) : RewardContractCommand, SignedCommandData {

        internal companion object {
            const val CONTRACT_RULE_INPUTS =
                "On reward issuing, zero reward states must be consumed."

            const val CONTRACT_RULE_OUTPUTS =
                "On reward issuing, only one reward state must be created."

            const val CONTRACT_RULE_REFERENCES =
                "On reward issuing, only one customer state must be referenced."

            const val CONTRACT_RULE_OWNER =
                "On reward issuing, the created reward's owner must be equal to the referenced customer's owner."

            const val CONTRACT_RULE_SIGNATURE_DATA =
                "On reward issuing, the signature data must be equal to the hash of the created reward state."

            const val CONTRACT_RULE_SIGNATURE_SIGNER =
                "On reward issuing, the signature data must be signed by the issuer of the created reward state."

            const val CONTRACT_RULE_SIGNERS =
                "On reward issuing, all participants of the created reward state must sign the transaction."
        }

        override fun verify(transaction: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
            val rewardInputs = transaction.inputsOfType<Reward>()
            val rewardOutputs = transaction.outputsOfType<Reward>()
            val customerReferences = transaction.referenceInputsOfType<Customer>()

            CONTRACT_RULE_INPUTS using (rewardInputs.isEmpty())
            CONTRACT_RULE_OUTPUTS using (rewardOutputs.size == 1)
            CONTRACT_RULE_REFERENCES using (customerReferences.size == 1)

            val rewardOutput = rewardOutputs.single()
            val customerReference = customerReferences.single()

            CONTRACT_RULE_OWNER using (rewardOutput.owner == customerReference.owner)
            CONTRACT_RULE_SIGNATURE_DATA using (rewardOutput.hash().bytes.contentEquals(signature.content))
            CONTRACT_RULE_SIGNATURE_SIGNER using (signature.verify(rewardOutput.issuer.owningKey))
            CONTRACT_RULE_SIGNERS using (rewardOutput.participants.owningKeys.all { it in signers })
        }
    }

    class Spend(override val signature: SignatureData) : RewardContractCommand, SignedCommandData {

        internal companion object {
            const val CONTRACT_RULE_INPUTS =
                "On reward spending, only one reward must be consumed."

            const val CONTRACT_RULE_OUTPUTS =
                "On reward spending, zero reward states must be created."

            const val CONTRACT_RULE_REFERENCES =
                "On reward spending, only one customer state must be referenced."

            const val CONTRACT_RULE_OWNER =
                "On reward spending, the consumed reward's owner must be equal to the referenced customer's owner."

            const val CONTRACT_RULE_SIGNATURE_DATA =
                "On reward spending, the signature data must be equal to the hash of the consumed reward state."

            const val CONTRACT_RULE_SIGNATURE_SIGNER =
                "On reward spending, the signature data must be signed by the owner of the reward state."

            const val CONTRACT_RULE_SIGNERS =
                "On reward spending, all participants of the consumed reward state must sign the transaction."
        }

        override fun verify(transaction: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
            val rewardInputs = transaction.inputsOfType<Reward>()
            val rewardOutputs = transaction.outputsOfType<Reward>()
            val customerReferences = transaction.referenceInputsOfType<Customer>()

            CONTRACT_RULE_INPUTS using (rewardInputs.size == 1)
            CONTRACT_RULE_OUTPUTS using (rewardOutputs.isEmpty())
            CONTRACT_RULE_REFERENCES using (customerReferences.size == 1)

            val rewardInput = rewardInputs.single()
            val customerReference = customerReferences.single()

            CONTRACT_RULE_OWNER using (rewardInput.owner == customerReference.owner)
            CONTRACT_RULE_SIGNATURE_DATA using (rewardInput.hash().bytes.contentEquals(signature.content))
            CONTRACT_RULE_SIGNATURE_SIGNER using (signature.verify(rewardInput.owner.owningKey))
            CONTRACT_RULE_SIGNERS using (rewardInput.participants.owningKeys.all { it in signers })
        }
    }
}
