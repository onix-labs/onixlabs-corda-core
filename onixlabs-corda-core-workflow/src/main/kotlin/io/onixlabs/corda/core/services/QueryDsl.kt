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

import io.onixlabs.corda.core.toTypedClass
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.QueryCriteria.*
import net.corda.core.node.services.vault.Sort
import net.corda.core.node.services.vault.SortAttribute
import net.corda.core.schemas.StatePersistable
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

/**
 * Represents a DSL for building vault queries.
 *
 * @param T The underlying [ContractState] which will be included in the query.
 * @param queryCriteria The query criteria to be built by the query DSL.
 * @param page The page specification which will be applied to the query.
 * @param sort The sorting which will be applied to the query.
 */
class QueryDsl<T : ContractState> internal constructor(
    private var queryCriteria: QueryCriteria = VaultQueryCriteria(),
    private var page: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    private var sort: Sort = DEFAULT_SORTING
) {
    val criteria: QueryCriteria get() = queryCriteria
    val paging: PageSpecification get() = page
    val sorting: Sort get() = sort

    /**
     * Specifies the state status of the query.
     *
     * @param status The status of the [ContractState] instances to apply to the query.
     */
    @QueryDslContext
    fun status(status: Vault.StateStatus) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withStatus(status)
    }

    /**
     * Specifies the contract state types to apply to the query criteria.
     *
     * @param contractStateTypes The [ContractState] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun contractStateTypes(contractStateTypes: Set<Class<out T>>) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withContractStateTypes(contractStateTypes)
    }

    /**
     * Specifies the contract state types to apply to the query criteria.
     *
     * @param contractStateTypes The [ContractState] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun contractStateTypes(vararg contractStateTypes: Class<out T>) {
        contractStateTypes(contractStateTypes.toSet())
    }

    /**
     * Specifies the state references to apply to the query criteria.
     *
     * @param stateRefs The [StateRef] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun stateRefs(stateRefs: List<StateRef>) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withStateRefs(stateRefs)
    }

    /**
     * Specifies the state references to apply to the query criteria.
     *
     * @param stateRefs The [StateRef] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun stateRefs(vararg stateRefs: StateRef) {
        stateRefs(stateRefs.toList())
    }

    /**
     * Specifies the notaries to apply to the query criteria.
     *
     * @param notaries The notary [AbstractParty] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun notaries(notaries: List<AbstractParty>) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withNotary(notaries)
    }

    /**
     * Specifies the notaries to apply to the query criteria.
     *
     * @param notaries The notary [AbstractParty] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun notaries(vararg notaries: AbstractParty) {
        notaries(notaries.toList())
    }

    /**
     * Specifies the soft locking condition of the query criteria.
     *
     * @param softLockingCondition The [SoftLockingCondition] to apply to the query criteria.
     */
    @QueryDslContext
    fun softLockingCondition(softLockingCondition: SoftLockingCondition) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withSoftLockingCondition(softLockingCondition)
    }

    /**
     * Specifies the time condition of the query criteria.
     *
     * @param timeCondition The [TimeCondition] to apply to the query criteria.
     */
    @QueryDslContext
    fun timeCondition(timeCondition: TimeCondition) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withTimeCondition(timeCondition)
    }

    /**
     * Specifies the relevancy status of the query criteria.
     *
     * @param relevancyStatus The [Vault.RelevancyStatus] to apply to the query criteria.
     */
    @QueryDslContext
    fun relevancyStatus(relevancyStatus: Vault.RelevancyStatus) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withRelevancyStatus(relevancyStatus)
    }

    /**
     * Specifies the constraint types to apply to the query criteria.
     *
     * @param constraintTypes The [Vault.ConstraintInfo.Type] to apply to the query criteria.
     */
    @QueryDslContext
    fun constraintTypes(constraintTypes: Set<Vault.ConstraintInfo.Type>) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withConstraintTypes(constraintTypes)
    }

    /**
     * Specifies the constraint types to apply to the query criteria.
     *
     * @param constraintTypes The [Vault.ConstraintInfo.Type] to apply to the query criteria.
     */
    @QueryDslContext
    fun constraintTypes(vararg constraintTypes: Vault.ConstraintInfo.Type) {
        constraintTypes(constraintTypes.toSet())
    }

    /**
     * Specifies the constraints to apply to the query criteria.
     *
     * @param constraints The [Vault.ConstraintInfo] to apply to the query criteria.
     */
    @QueryDslContext
    fun constraints(constraints: Set<Vault.ConstraintInfo>) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withConstraints(constraints)
    }

    /**
     * Specifies the constraints to apply to the query criteria.
     *
     * @param constraints The [Vault.ConstraintInfo] to apply to the query criteria.
     */
    @QueryDslContext
    fun constraints(vararg constraints: Vault.ConstraintInfo) {
        constraints(constraints.toSet())
    }

    /**
     * Specifies the participants to apply to the query criteria.
     *
     * @param participants The participant [AbstractParty] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun participants(participants: List<AbstractParty>) {
        queryCriteria = (queryCriteria as VaultQueryCriteria).withParticipants(participants)
    }

    /**
     * Specifies the participants to apply to the query criteria.
     *
     * @param participants The participant [AbstractParty] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun participants(vararg participants: AbstractParty) {
        participants(participants.toList())
    }

    /**
     * Specifies the unique identifiers to apply to the query criteria.
     *
     * @param linearIds The [UniqueIdentifier] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun linearIds(linearIds: List<UniqueIdentifier>) {
        queryCriteria = queryCriteria.and(LinearStateQueryCriteria(linearId = linearIds))
    }

    /**
     * Specifies the unique identifiers to apply to the query criteria.
     *
     * @param linearIds The [UniqueIdentifier] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun linearIds(vararg linearIds: UniqueIdentifier) {
        linearIds(linearIds.toList())
    }

    /**
     * Specifies custom query criteria to apply to the parent query criteria.
     *
     * @param criteria The custom criteria to apply to the parent query criteria.
     */
    @QueryDslContext
    fun where(criteria: QueryCriteria) {
        queryCriteria = queryCriteria.and(criteria)
    }

    /**
     * Specifies that the contents of the action block must be applied to the query criteria using an AND logical operator.
     *
     * @param action The action which will build a sub-query criteria to be applied to the parent query criteria.
     */
    @QueryDslContext
    fun and(action: QueryDsl<T>.() -> Unit) {
        val queryDsl = QueryDsl<T>()
        action(queryDsl)
        queryCriteria = queryCriteria.and(queryDsl.criteria)
    }

    /**
     * Specifies that the contents of the action block must be applied to the query criteria using an OR logical operator.
     *
     * @param action The action which will build a sub-query criteria to be applied to the parent query criteria.
     */
    @QueryDslContext
    fun or(action: QueryDsl<T>.() -> Unit) {
        val queryDsl = QueryDsl<T>()
        action(queryDsl)
        queryCriteria = queryCriteria.or(queryDsl.criteria)
    }

    /**
     * Specifies the page specification which will be applied to the query.
     *
     * @param pageSpecification The [PageSpecification] to apply to the query.
     */
    @QueryDslContext
    fun page(pageSpecification: PageSpecification) {
        page = pageSpecification
    }

    /**
     * Specifies the page specification which will be applied to the query.
     *
     * @param pageNumber The page number of the page to obtain from the vault.
     * @param pageSize The size of the page to obtain from the vault.
     */
    @QueryDslContext
    fun page(pageNumber: Int, pageSize: Int) {
        page(PageSpecification(pageNumber, pageSize))
    }

    /**
     * Specifies the sorting which will be applied to the query.
     *
     * @param sort The [Sort] to apply to the query.
     */
    @QueryDslContext
    fun sort(sort: Sort) {
        this.sort = sort
    }

    /**
     * Specifies that the query should be sorted by the specified property and by the specified order.
     *
     * @param property The property to sort the query by.
     * @param direction The sort direction of the sorted data.
     */
    @QueryDslContext
    fun sortBy(property: KProperty1<out StatePersistable, *>, direction: Sort.Direction) {
        val receiver = property.parameters.first().type.javaType.toTypedClass<StatePersistable>()
        val attribute = SortAttribute.Custom(receiver, property.name)
        val column = Sort.SortColumn(attribute, direction)
        sort(Sort(setOf(column)))
    }

    /**
     * Specifies that the query should be sorted by the specified property in ascending order.
     *
     * @param property The property to sort the query by.
     */
    @QueryDslContext
    fun sortByAscending(property: KProperty1<out StatePersistable, *>) {
        sortBy(property, Sort.Direction.ASC)
    }

    /**
     * Specifies that the query should be sorted by the specified property in descending order.
     *
     * @param property The property to sort the query by.
     */
    @QueryDslContext
    fun sortByDescending(property: KProperty1<out StatePersistable, *>) {
        sortBy(property, Sort.Direction.DESC)
    }
}
