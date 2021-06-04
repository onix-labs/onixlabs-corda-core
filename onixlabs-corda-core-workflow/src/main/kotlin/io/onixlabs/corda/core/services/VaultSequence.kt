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

    internal constructor(service: VaultService<T>, queryDsl: QueryDsl<T>)
            : this(service, queryDsl.criteria, queryDsl.paging, queryDsl.sorting)

    /**
     * Gets the iterator which will evaluate the vault query.
     *
     * @return Returns an iterator for the [StateAndRef] instances returned by the vault query.
     */
    override fun iterator(): Iterator<StateAndRef<T>> = service.queryBy(criteria, paging, sorting).states.iterator()
}
