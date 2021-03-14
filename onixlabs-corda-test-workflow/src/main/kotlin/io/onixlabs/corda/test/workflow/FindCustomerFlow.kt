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

package io.onixlabs.corda.test.workflow

import io.onixlabs.corda.core.query.*
import io.onixlabs.corda.test.contract.Customer
import io.onixlabs.corda.test.contract.CustomerEntity
import net.corda.core.contracts.StateRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import java.time.Instant

class FindCustomerFlow(
    owner: QueryEquatable<AbstractParty>? = null,
    firstName: QueryString<String>? = null,
    lastName: QueryString<String>? = null,
    birthday: QueryComparable<Instant>? = null,
    previousStateRef: QueryEquatable<StateRef?>? = null,
    linearId: QueryEquatable<UniqueIdentifier>? = null,
    externalId: QueryString<String?>? = null,
    stateStatus: Vault.StateStatus = Vault.StateStatus.UNCONSUMED,
    relevancyStatus: Vault.RelevancyStatus = Vault.RelevancyStatus.ALL,
    override val pageSpecification: PageSpecification = DEFAULT_PAGE_SPECIFICATION
) : FindStateFlow<Customer>() {
    override val criteria: QueryCriteria = QueryCriteria.VaultQueryCriteria(
        contractStateTypes = setOf(contractStateType),
        relevancyStatus = relevancyStatus,
        status = stateStatus
    ).andWithExpressions(
        owner?.toExpression(CustomerEntity::owner) { it },
        firstName?.toExpression(CustomerEntity::firstName) { it },
        lastName?.toExpression(CustomerEntity::lastName) { it },
        birthday?.toExpression(CustomerEntity::birthday) { it },
        previousStateRef?.toExpression(CustomerEntity::previousStateRef) { it?.toString() },
        linearId?.toExpression(CustomerEntity::linearId) { it.id },
        linearId?.toExpression(CustomerEntity::externalId) { it.externalId },
        externalId?.toExpression(CustomerEntity::externalId) { it }
    )
}
