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

import net.corda.core.crypto.NullKeys.NULL_PARTY
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.time.Instant
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "onixlabs_customer_states")
class CustomerEntity(
    @Column(name = "linear_id", nullable = false)
    val linearId: UUID = UUID.randomUUID(),

    @Column(name = "external_id", nullable = true)
    val externalId: String? = null,

    @Column(name = "owner", nullable = false)
    val owner: AbstractParty = NULL_PARTY,

    @Column(name = "first_name", nullable = false)
    val firstName: String = "",

    @Column(name = "last_name", nullable = false)
    val lastName: String = "",

    @Column(name = "birthday", nullable = true)
    val birthday: Instant = Instant.MIN,

    /**
     * Since chain state previous state references should start at null,
     * including this in the schema allows queries for all new/un-evolved states.
     */
    @Column(name = "previous_state_ref")
    val previousStateRef: String? = null,

    @Column(name = "hash", nullable = false, unique = true)
    val hash: String = ""
) : PersistentState() {

    companion object CustomerSchema {
        object CustomerSchemaV1 : MappedSchema(CustomerSchema::class.java, 1, listOf(CustomerEntity::class.java)) {
            override val migrationResource = "customer-schema.changelog-master"
        }
    }
}
