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
import net.corda.core.contracts.StateAndRef
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort

/**
 * Represents a lazily evaluated sequence of vault query results.
 *
 * @param T The underlying [ContractState] type bound to this sequence.
 * @property service A reference to the vault service which will be used to perform vault queries.
 * @property criteria The criteria that will be used to query the vault.
 * @property paging The page specification consisting of the page number and page size to return from the vault.
 * @property sorting The sort order for the resulting data.
 */
class VaultSequence<T : ContractState> internal constructor(
    private val service: VaultService<T>,
    private val criteria: QueryCriteria,
    private val paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    private val sorting: Sort = DEFAULT_SORTING
) : Sequence<StateAndRef<T>> {

    internal constructor(service: VaultService<T>, queryDsl: QueryDsl<T>) : this(
        service = service,
        criteria = queryDsl.getQueryCriteria(),
        paging = queryDsl.getPaging(),
        sorting = queryDsl.getSorting()
    )

    /**
     * Gets the iterator which will evaluate the vault query.
     *
     * @return Returns an iterator for the [StateAndRef] instances returned by the vault query.
     */
    override fun iterator(): Iterator<StateAndRef<T>> {
        return service.queryBy(criteria, paging, sorting).states.iterator()
    }
}
