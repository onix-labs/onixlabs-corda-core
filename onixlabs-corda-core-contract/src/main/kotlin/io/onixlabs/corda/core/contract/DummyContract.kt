/**
 * Copyright 2020 Matthew Layton
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

package io.onixlabs.corda.core.contract

import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

/**
 * Represents a dummy state and contract that will never be used.
 * This exists for two reasons:
 * 1. So that Corda will load the contract into attachment storage.
 * 2. To test contract interface implementations locally.
 */
@Suppress("UNUSED")
internal class DummyContract : Contract {

    companion object : ContractID

    @BelongsToContract(DummyContract::class)
    data class DummyState(
        override val participants: List<AbstractParty> = emptyList(),
        override val previousStateRef: StateRef? = null
    ) : ChainState

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<DummyContractCommand>()
        when (command.value) {
            is DummyCommand -> command.value.verify(tx, command.signers.toSet())
            else -> throw IllegalArgumentException("Unrecognised command: ${command.value}.")
        }
    }

    interface DummyContractCommand : SignedCommandData, VerifiedCommandData

    class DummyCommand(override val signature: SignatureData) : DummyContractCommand {

        companion object {
            internal const val CONTRACT_RULE_COMMAND_SIGNED =
                "On dummy command, the command must be signed by the dummy state participant."
        }

        override fun verify(transaction: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
            val state = transaction.singleOutputOfType<DummyState>()
            val command = transaction.singleCommandOfType<DummyCommand>()

            val key = state.participants.single().owningKey
            val signature = command.value.signature

            CONTRACT_RULE_COMMAND_SIGNED using (signature.verify(key))
        }
    }
}
