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
import io.onixlabs.corda.test.contract.Reward
import io.onixlabs.corda.test.contract.RewardEntity
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort

class FindRewardsFlow(
    issuer: QueryEquatable<AbstractParty>? = null,
    owner: QueryEquatable<AbstractParty>? = null,
    points: QueryComparable<Int>? = null,
    customerLinearId: QueryEquatable<UniqueIdentifier>? = null,
    customerExternalId: QueryString<String?>? = null,
    stateStatus: Vault.StateStatus = Vault.StateStatus.UNCONSUMED,
    relevancyStatus: Vault.RelevancyStatus = Vault.RelevancyStatus.ALL,
    override val pageSpecification: PageSpecification = DEFAULT_PAGE_SPECIFICATION,
    override val sorting: Sort = DEFAULT_SORTING
) : FindStatesFlow<Reward>() {
    override val criteria: QueryCriteria = QueryCriteria.VaultQueryCriteria(
        contractStateTypes = setOf(contractStateType),
        relevancyStatus = relevancyStatus,
        status = stateStatus
    ).andWithExpressions(
        issuer?.toExpression(RewardEntity::issuer) { it },
        owner?.toExpression(RewardEntity::owner) { it },
        points?.toExpression(RewardEntity::points) { it },
        customerLinearId?.toExpression(RewardEntity::customerLinearId) { it.id },
        customerLinearId?.toExpression(RewardEntity::customerExternalId) { it.externalId },
        customerExternalId?.toExpression(RewardEntity::customerExternalId) { it }
    )
}
