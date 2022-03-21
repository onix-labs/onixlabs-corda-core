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

package io.onixlabs.corda.core.services

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort
import java.util.*

/**
 * Determines whether the sequence contains any elements.
 *
 * @param T the underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @return Returns true if the sequence contains any elements; otherwise, false.
 */
fun <T : ContractState> VaultService<T>.any(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): Boolean = VaultSequence(this, criteria, paging, sorting).any()

/**
 * Determines whether the sequence contains any elements.
 *
 * @param T the underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns true if the sequence contains any elements; otherwise, false.
 */
fun <T : ContractState> VaultService<T>.any(action: QueryDsl<T>.() -> Unit): Boolean {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl).any()
}

/**
 * Counts the number of elements in a sequence.
 *
 * @param T the underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @return Returns a count of the number of elements in a sequence.
 */
fun <T : ContractState> VaultService<T>.count(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): Int = VaultSequence(this, criteria, paging, sorting).count()

/**
 * Counts the number of elements in a sequence.
 *
 * @param T the underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns a count of the number of elements in a sequence.
 */
fun <T : ContractState> VaultService<T>.count(action: QueryDsl<T>.() -> Unit): Int {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl).count()
}

/**
 * Filters a sequence of elements based on the specified query criteria.
 *
 * @param T the underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns a filtered sequence of elements.
 */
fun <T : ContractState> VaultService<T>.filter(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): VaultSequence<T> = VaultSequence(this, criteria, paging, sorting)

/**
 * Filters a sequence of values based on the specified query criteria.
 *
 * @param T The underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns a filtered sequence of elements.
 */
fun <T : ContractState> VaultService<T>.filter(action: QueryDsl<T>.() -> Unit): VaultSequence<T> {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl)
}

/**
 * Obtains the first element of a sequence.
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns the first element in the sequence.
 */
fun <T : ContractState> VaultService<T>.first(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): StateAndRef<T> = VaultSequence(this, criteria, paging, sorting).first()

/**
 * Obtains the first element of a sequence.
 *
 * @param T The underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns the first element in the sequence.
 */
fun <T : ContractState> VaultService<T>.first(action: QueryDsl<T>.() -> Unit): StateAndRef<T> {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl).first()
}

/**
 * Obtains the first element of a sequence, or null if no element is found.
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns the first element in the sequence, or null if no element is found.
 */
fun <T : ContractState> VaultService<T>.firstOrNull(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): StateAndRef<T>? = VaultSequence(this, criteria, paging, sorting).firstOrNull()

/**
 * Obtains the first element of a sequence, or null if no element is found.
 *
 * @param T The underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns the first element in the sequence, or null if no element is found.
 */
fun <T : ContractState> VaultService<T>.firstOrNull(action: QueryDsl<T>.() -> Unit): StateAndRef<T>? {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl).firstOrNull()
}

/**
 * Obtains the last element of a sequence.
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns the last element in the sequence.
 */
fun <T : ContractState> VaultService<T>.last(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): StateAndRef<T> = VaultSequence(this, criteria, paging, sorting).last()

/**
 * Obtains the last element of a sequence.
 *
 * @param T The underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns the last element in the sequence.
 */
fun <T : ContractState> VaultService<T>.last(action: QueryDsl<T>.() -> Unit): StateAndRef<T> {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl).last()
}

/**
 * Obtains the last element of a sequence, or null if no element is found.
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns the last element in the sequence, or null if no element is found.
 */
fun <T : ContractState> VaultService<T>.lastOrNull(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): StateAndRef<T>? = VaultSequence(this, criteria, paging, sorting).lastOrNull()

/**
 * Obtains the last element of a sequence, or null if no element is found.
 *
 * @param T The underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns the last element in the sequence, or null if no element is found.
 */
fun <T : ContractState> VaultService<T>.lastOrNull(action: QueryDsl<T>.() -> Unit): StateAndRef<T>? {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl).lastOrNull()
}

/**
 * Obtains a single, specific element of a sequence.
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns the last element in the sequence.
 */
fun <T : ContractState> VaultService<T>.single(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): StateAndRef<T> = VaultSequence(this, criteria, paging, sorting).single()

/**
 * Obtains a single, specific element of a sequence.
 *
 * @param T The underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns the last element in the sequence.
 */
fun <T : ContractState> VaultService<T>.single(action: QueryDsl<T>.() -> Unit): StateAndRef<T> {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl).single()
}

/**
 * Obtains a single, specific element of a sequence, or null if no element is found.
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns the last element in the sequence, or null if no element is found.
 */
fun <T : ContractState> VaultService<T>.singleOrNull(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): StateAndRef<T>? = VaultSequence(this, criteria, paging, sorting).singleOrNull()

/**
 * Obtains a single, specific element of a sequence, or null if no element is found.
 *
 * @param T The underlying [ContractState] type.
 * @param action Builds the query criteria that will be used to obtain the sequence.
 * @return Returns the last element in the sequence, or null if no element is found.
 */
fun <T : ContractState> VaultService<T>.singleOrNull(action: QueryDsl<T>.() -> Unit): StateAndRef<T>? {
    val queryDsl = QueryDsl<T>(defaultQueryCriteria)
    action(queryDsl)
    return VaultSequence(this, queryDsl).singleOrNull()
}

/**
 * Obtains a sequence of elements as a [List].
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns a sequence of elements as a [List].
 */
fun <T : ContractState> VaultService<T>.toList(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): List<StateAndRef<T>> = VaultSequence(this, criteria, paging, sorting).toList()

/**
 * Obtains a sequence of elements as a [Set].
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the sequence.
 * @param paging The page specification that will be applied to the vault query.
 * @param sorting The sort that will be applied to the vault query.
 * @return Returns a sequence of elements as a [Set].
 */
fun <T : ContractState> VaultService<T>.toSet(
    criteria: QueryCriteria = defaultQueryCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING
): Set<StateAndRef<T>> = VaultSequence(this, criteria, paging, sorting).toSet()

/**
 * Subscribes to vault tracking updates.
 *
 * @param T The underlying [ContractState] type.
 * @param criteria The query criteria that will be used to obtain the tracking updates.
 * @param paging The page specification that will be applied to the vault tracking.
 * @param sorting The sort that will be applied to the vault tracking.
 * @param observer The function which will receive vault tracking updates as [VaultObservable] instances.
 */
fun <T : ContractState> VaultService<T>.subscribe(
    criteria: QueryCriteria = defaultTrackingCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING,
    observer: (VaultObservable<T>) -> Unit
) {
    fun handle(stateAndRef: StateAndRef<T>, status: Vault.StateStatus, flowId: UUID?) {
        if (stateAndRef.state.data.javaClass.isAssignableFrom(contractStateType)) {
            observer(VaultObservable(stateAndRef.state, stateAndRef.ref, status, flowId))
        }
    }

    val status = when (criteria) {
        is QueryCriteria.VaultQueryCriteria -> criteria.status
        else -> Vault.StateStatus.ALL
    }

    trackBy(criteria, paging, sorting).updates.subscribe {
        if (status != Vault.StateStatus.UNCONSUMED) {
            it.consumed.forEach { stateAndRef -> handle(stateAndRef, Vault.StateStatus.CONSUMED, it.flowId) }
        }

        if (status != Vault.StateStatus.CONSUMED) {
            it.produced.forEach { stateAndRef -> handle(stateAndRef, Vault.StateStatus.UNCONSUMED, it.flowId) }
        }
    }
}
