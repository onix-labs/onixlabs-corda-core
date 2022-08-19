/*
 * Copyright 2020-2022 ONIXLabs
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

import io.onixlabs.corda.core.contract.AbstractSingularResolvable
import io.onixlabs.corda.core.contract.SingularResolvable
import io.onixlabs.corda.core.services.vaultQuery
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

@BelongsToContract(RewardContract::class)
data class Reward(
    val issuer: AbstractParty,
    val owner: AbstractParty,
    val points: Int,
    val customerLinearId: UniqueIdentifier
) : QueryableState {

    val customer: SingularResolvable<Customer> get() = CustomerResolver(this)

    override val participants: List<AbstractParty>
        get() = setOf(issuer, owner).toList()

    override fun generateMappedObject(schema: MappedSchema): PersistentState = when (schema) {
        is RewardEntity.RewardSchema.RewardSchemaV1 -> RewardEntity(
            issuer = issuer,
            owner = owner,
            points = points,
            customerLinearId = customerLinearId.id,
            customerExternalId = customerLinearId.externalId
        )
        else -> throw IllegalArgumentException("Unrecognised schema: $schema.")
    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(RewardEntity.RewardSchema.RewardSchemaV1)
    }

    private class CustomerResolver(private val reward: Reward) : AbstractSingularResolvable<Customer>() {

        override val criteria: QueryCriteria get() = vaultQuery<Customer> {
            relevancyStatus(Vault.RelevancyStatus.ALL)
            linearIds(reward.customerLinearId)
        }

        override fun isPointingTo(stateAndRef: StateAndRef<Customer>): Boolean {
            return stateAndRef.state.data.linearId == reward.customerLinearId
        }
    }
}
