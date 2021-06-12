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

import net.corda.core.crypto.NullKeys.NULL_PARTY
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "onixlabs_reward_states")
class RewardEntity(
    @Column(name = "issuer", nullable = false)
    val issuer: AbstractParty = NULL_PARTY,

    @Column(name = "owner", nullable = false)
    val owner: AbstractParty = NULL_PARTY,

    @Column(name = "points", nullable = false)
    val points: Int = 0,

    @Column(name = "customer_linear_id", nullable = false)
    val customerLinearId: UUID = UUID.randomUUID(),

    @Column(name = "customer_external_id", nullable = true)
    val customerExternalId: String? = null
) : PersistentState() {

    companion object RewardSchema {
        object RewardSchemaV1 : MappedSchema(RewardSchema::class.java, 1, listOf(RewardEntity::class.java)) {
            override val migrationResource = "reward-schema.changelog-master"
        }
    }
}
