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

package io.onixlabs.corda.core.services

import net.corda.core.contracts.ContractState
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort

/**
 * Creates a [QueryCriteria] using the Query DSL.
 *
 * @param T The underlying [ContractState] which will be included in the query.
 * @param contractStateType The [ContractState] class which will be included in the query.
 * @param stateStatus The state status which will be applied to the query.
 * @param relevancyStatus The relevancy status which will be applied to the query.
 * @param page The page specification which will be applied to the query.
 * @param sort The sorting which will be applied to the query.
 * @return Returns the [QueryCriteria] that was created using the Query DSL.
 */
@QueryDslContext
fun <T : ContractState> vaultQuery(
    contractStateType: Class<T>,
    stateStatus: Vault.StateStatus = Vault.StateStatus.UNCONSUMED,
    relevancyStatus: Vault.RelevancyStatus = Vault.RelevancyStatus.RELEVANT,
    page: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sort: Sort = DEFAULT_SORTING,
    action: QueryDsl<T>.() -> Unit
): QueryCriteria {
    val criteria = QueryCriteria.VaultQueryCriteria(
        contractStateTypes = setOf(contractStateType),
        status = stateStatus,
        relevancyStatus = relevancyStatus
    )

    return vaultQuery(criteria, page, sort, action)
}

/**
 * Creates a [QueryCriteria] using the Query DSL.
 *
 * @param T The underlying [ContractState] which will be included in the query.
 * @param stateStatus The state status which will be applied to the query.
 * @param relevancyStatus The relevancy status which will be applied to the query.
 * @param page The page specification which will be applied to the query.
 * @param sort The sorting which will be applied to the query.
 * @return Returns the [QueryCriteria] that was created using the Query DSL.
 */
@QueryDslContext
inline fun <reified T : ContractState> vaultQuery(
    stateStatus: Vault.StateStatus = Vault.StateStatus.UNCONSUMED,
    relevancyStatus: Vault.RelevancyStatus = Vault.RelevancyStatus.RELEVANT,
    page: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sort: Sort = DEFAULT_SORTING,
    noinline action: QueryDsl<T>.() -> Unit
): QueryCriteria {
    return vaultQuery(T::class.java, stateStatus, relevancyStatus, page, sort, action)
}

/**
 * Creates a [QueryCriteria] using the Query DSL.
 *
 * @param T The underlying [ContractState] which will be included in the query.
 * @param criteria The base criteria upon which the Query DSL will build.
 * @param page The page specification which will be applied to the query.
 * @param sort The sorting which will be applied to the query.
 * @return Returns the [QueryCriteria] that was created using the Query DSL.
 */
private fun <T : ContractState> vaultQuery(
    criteria: QueryCriteria,
    page: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sort: Sort = DEFAULT_SORTING,
    action: QueryDsl<T>.() -> Unit
): QueryCriteria {
    val queryDsl = QueryDsl<T>(criteria, page, sort)
    action(queryDsl)
    return queryDsl.criteria
}
