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

import io.onixlabs.corda.core.contract.ContractID
import io.onixlabs.corda.core.contract.VerifiedCommandData
import io.onixlabs.corda.core.contract.allowCommands
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

class CustomerContract : Contract {

    companion object : ContractID

    override fun verify(tx: LedgerTransaction) = tx.allowCommands(
        Issue::class.java,
        Amend::class.java,
        Revoke::class.java
    )

    interface CustomerContractCommand : VerifiedCommandData

    object Issue : CustomerContractCommand {

        internal const val CONTRACT_RULE_INPUTS =
            "On customer issuing, zero customer states must be consumed."

        internal const val CONTRACT_RULE_OUTPUTS =
            "On customer issuing, only one customer state must be created."

        internal const val CONTRACT_RULE_FIRST_NAME =
            "On customer issuing, the created customer's first name must not be blank."

        internal const val CONTRACT_RULE_LAST_NAME =
            "On customer issuing, the created customer's last name must not be blank."

        internal const val CONTRACT_RULE_PREVIOUS_STATE_REF =
            "On customer issuing, the created customer's previous state reference must be null."

        internal const val CONTRACT_RULE_SIGNERS =
            "On customer issuing, the created customer's owner must sign the transaction."

        override fun verify(transaction: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
            val customerInputs = transaction.inRefsOfType<Customer>()
            val customerOutputs = transaction.outputsOfType<Customer>()

            CONTRACT_RULE_INPUTS using (customerInputs.isEmpty())
            CONTRACT_RULE_OUTPUTS using (customerOutputs.size == 1)

            val customerOutput = customerOutputs.single()

            CONTRACT_RULE_FIRST_NAME using (customerOutput.firstName.isNotBlank())
            CONTRACT_RULE_LAST_NAME using (customerOutput.lastName.isNotBlank())
            CONTRACT_RULE_PREVIOUS_STATE_REF using (customerOutput.previousStateRef == null)
            CONTRACT_RULE_SIGNERS using (customerOutput.owner.owningKey in signers)
        }
    }

    object Amend : CustomerContractCommand {

        internal const val CONTRACT_RULE_INPUTS =
            "On customer amending, only one customer state must be consumed."

        internal const val CONTRACT_RULE_OUTPUTS =
            "On customer amending, only one customer state must be created."

        internal const val CONTRACT_RULE_FIRST_NAME =
            "On customer amending, the created customer's first name must not be blank."

        internal const val CONTRACT_RULE_LAST_NAME =
            "On customer amending, the created customer's last name must not be blank."

        internal const val CONTRACT_RULE_PREVIOUS_STATE_REF =
            "On customer amending, the created customer's previous state reference must be equal to the consumed customer's state reference."

        internal const val CONTRACT_RULE_SIGNERS =
            "On customer amending, the created customer's owner must sign the transaction."

        override fun verify(transaction: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
            val customerInputs = transaction.inRefsOfType<Customer>()
            val customerOutputs = transaction.outputsOfType<Customer>()

            CONTRACT_RULE_INPUTS using (customerInputs.size == 1)
            CONTRACT_RULE_OUTPUTS using (customerOutputs.size == 1)

            val customerInput = customerInputs.single()
            val customerOutput = customerOutputs.single()

            CONTRACT_RULE_FIRST_NAME using (customerOutput.firstName.isNotBlank())
            CONTRACT_RULE_LAST_NAME using (customerOutput.lastName.isNotBlank())
            CONTRACT_RULE_PREVIOUS_STATE_REF using (customerOutput.previousStateRef == customerInput.ref)
            CONTRACT_RULE_SIGNERS using (customerOutput.owner.owningKey in signers)
        }
    }

    object Revoke : CustomerContractCommand {

        internal const val CONTRACT_RULE_INPUTS =
            "On customer revoking, only one customer state must be consumed."

        internal const val CONTRACT_RULE_OUTPUTS =
            "On customer revoking, zero customer states must be created."

        internal const val CONTRACT_RULE_SIGNERS =
            "On customer revoking, the consumed customer's owner must sign the transaction."

        override fun verify(transaction: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
            val customerInputs = transaction.inRefsOfType<Customer>()
            val customerOutputs = transaction.outputsOfType<Customer>()

            CONTRACT_RULE_INPUTS using (customerInputs.size == 1)
            CONTRACT_RULE_OUTPUTS using (customerOutputs.isEmpty())

            val customerInput = customerInputs.single()

            CONTRACT_RULE_SIGNERS using (customerInput.state.data.owner.owningKey in signers)
        }
    }
}
