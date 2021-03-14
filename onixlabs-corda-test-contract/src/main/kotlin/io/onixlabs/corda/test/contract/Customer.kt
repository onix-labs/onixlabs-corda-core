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

import io.onixlabs.corda.core.contract.AbstractPluralResolvable
import io.onixlabs.corda.core.contract.ChainState
import io.onixlabs.corda.core.contract.Hashable
import io.onixlabs.corda.core.contract.PluralResolvable
import net.corda.core.contracts.*
import net.corda.core.crypto.SecureHash
import net.corda.core.identity.AbstractParty
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.time.Instant

@BelongsToContract(CustomerContract::class)
data class Customer(
    val owner: AbstractParty,
    val firstName: String,
    val lastName: String,
    val birthday: Instant,
    override val previousStateRef: StateRef? = null,
    override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState, ChainState, Hashable {

    val rewards: PluralResolvable<Reward> get() = RewardResolver(this)

    override val hash: SecureHash
        get() = SecureHash.sha256("$firstName$lastName$birthday$previousStateRef")

    override val participants: List<AbstractParty>
        get() = listOf(owner)

    override fun generateMappedObject(schema: MappedSchema): PersistentState = when (schema) {
        is CustomerEntity.CustomerSchema.CustomerSchemaV1 -> CustomerEntity(
            linearId = linearId.id,
            externalId = linearId.externalId,
            owner = owner,
            firstName = firstName,
            lastName = lastName,
            birthday = birthday,
            previousStateRef = previousStateRef?.toString(),
            hash = hash.toString()
        )
        else -> throw IllegalArgumentException("Unrecognised schema: $schema.")
    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(CustomerEntity.CustomerSchema.CustomerSchemaV1)
    }

    private class RewardResolver(private val customer: Customer) : AbstractPluralResolvable<Reward>() {

        override val criteria: QueryCriteria = QueryCriteria.VaultQueryCriteria(
            contractStateTypes = setOf(Reward::class.java),
            status = Vault.StateStatus.UNCONSUMED,
            relevancyStatus = Vault.RelevancyStatus.RELEVANT
        ).andWithExpressions(
            RewardEntity::owner.equal(customer.owner),
            RewardEntity::customerLinearId.equal(customer.linearId.id),
            RewardEntity::customerExternalId.equal(customer.linearId.externalId)
        )

        override fun isPointingTo(stateAndRef: StateAndRef<Reward>): Boolean = with(stateAndRef.state) {
            data.customerLinearId == customer.linearId && data.owner == customer.owner
        }
    }
}
