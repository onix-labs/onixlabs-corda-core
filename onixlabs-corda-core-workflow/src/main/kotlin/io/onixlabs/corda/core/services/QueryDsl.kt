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
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.*
import net.corda.core.node.services.vault.CriteriaExpression.ColumnPredicateExpression
import net.corda.core.node.services.vault.QueryCriteria.*
import net.corda.core.schemas.StatePersistable
import net.corda.core.utilities.OpaqueBytes
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaType

/**
 * Represents a DSL for building expressive Corda vault queries.
 *
 * @param T The underlying [ContractState] to determine which contract state types to include in the query.
 * @property criteria The root query criteria upon which the query is constructed.
 * @property paging The page specification which will be applied to the query.
 * @property sorting The sorting which will be applied to the query.
 */
class QueryDsl<T : ContractState> internal constructor(
    private var criteria: QueryCriteria,
    private var paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    private var sorting: Sort = DEFAULT_SORTING
) {

    /**
     * Defines the common query criteria which will be applied to each sub-node of the query expression.
     *
     * Values applied to this should be initialized up front, and will be copied to all sub-nodes of the
     * criteria expression. If you want to include different common query criteria to sub-query expressions
     * then wrap the expression into either and "and" or "or" block, which will give you a new [QueryDsl] instance
     * and therefore a new set of common query criteria.
     */
    private val commonQueryCriteria = object : CommonQueryCriteria() {
        override var constraintTypes: Set<Vault.ConstraintInfo.Type> = emptySet()
        override var constraints: Set<Vault.ConstraintInfo> = emptySet()
        override var contractStateTypes: Set<Class<out ContractState>>? = null
        override var exactParticipants: List<AbstractParty>? = null
        override var externalIds: List<UUID> = emptyList()
        override var participants: List<AbstractParty>? = null
        override var relevancyStatus: Vault.RelevancyStatus = Vault.RelevancyStatus.RELEVANT
        override var status: Vault.StateStatus = Vault.StateStatus.UNCONSUMED
    }

    init {

        /**
         * If the root query criteria derives from CommonQueryCriteria then these values are copied to
         * the common query criteria on initialization.
         */
        if (criteria is CommonQueryCriteria) with(commonQueryCriteria) {
            val criteriaToCopy = criteria as CommonQueryCriteria
            constraintTypes = criteriaToCopy.constraintTypes
            constraints = criteriaToCopy.constraints
            contractStateTypes = criteriaToCopy.contractStateTypes
            exactParticipants = criteriaToCopy.exactParticipants
            externalIds = criteriaToCopy.externalIds
            participants = criteriaToCopy.participants
            relevancyStatus = criteriaToCopy.relevancyStatus
            status = criteriaToCopy.status
        }
    }

    /**
     * Specifies the constraints to apply to the query criteria.
     *
     * @param values The [Vault.ConstraintInfo] to apply to the query criteria.
     */
    @QueryDslContext
    fun constraints(values: Iterable<Vault.ConstraintInfo>) {
        commonQueryCriteria.constraints = values.toSet()
        criteria = criteria.updateQueryCriteria()
    }

    /**
     * Specifies the constraints to apply to the query criteria.
     *
     * @param values The [Vault.ConstraintInfo] to apply to the query criteria.
     */
    @QueryDslContext
    fun constraints(vararg values: Vault.ConstraintInfo) {
        constraints(values.toSet())
    }

    /**
     * Specifies the constraint types to apply to the query criteria.
     *
     * @param values The [Vault.ConstraintInfo.Type] to apply to the query criteria.
     */
    @QueryDslContext
    fun constraintTypes(values: Iterable<Vault.ConstraintInfo.Type>) {
        commonQueryCriteria.constraintTypes = values.toSet()
        criteria = criteria.updateQueryCriteria()
    }

    /**
     * Specifies the constraint types to apply to the query criteria.
     *
     * @param values The [Vault.ConstraintInfo.Type] to apply to the query criteria.
     */
    @QueryDslContext
    fun constraintTypes(vararg values: Vault.ConstraintInfo.Type) {
        constraintTypes(values.toSet())
    }

    /**
     * Specifies the contract state types to apply to the query criteria.
     *
     * @param values The [ContractState] types to apply to the query criteria.
     */
    @QueryDslContext
    fun contractStateTypes(values: Iterable<Class<out T>>?) {
        commonQueryCriteria.contractStateTypes = values?.toSet()
        criteria = criteria.updateQueryCriteria()
    }

    /**
     * Specifies the contract state types to apply to the query criteria.
     *
     * @param values The [ContractState] types to apply to the query criteria.
     */
    @QueryDslContext
    fun contractStateTypes(vararg values: Class<out T>) {
        contractStateTypes(values.toSet())
    }

    /**
     * Specifies the exact participants to apply to the query criteria.
     *
     * @param values The exact [AbstractParty] participants to apply to the query.
     */
    @QueryDslContext
    fun exactParticipants(values: Iterable<AbstractParty>?) {
        commonQueryCriteria.exactParticipants = values?.toList()
        criteria = criteria.updateQueryCriteria()
    }

    /**
     * Specifies the exact participants to apply to the query criteria.
     *
     * @param values The exact [AbstractParty] participants to apply to the query.
     */
    @QueryDslContext
    fun exactParticipants(vararg values: AbstractParty) {
        exactParticipants(values.toList())
    }

    /**
     * Specifies the participants to apply to the query criteria.
     *
     * @param values The participant [AbstractParty] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun participants(values: Iterable<AbstractParty>?) {
        commonQueryCriteria.participants = values?.toList()
        criteria = criteria.updateQueryCriteria()
    }

    /**
     * Specifies the participants to apply to the query criteria.
     *
     * @param values The participant [AbstractParty] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun participants(vararg values: AbstractParty) {
        participants(values.toList())
    }

    /**
     * Specifies the relevancy status of the query criteria.
     *
     * @param value The [Vault.RelevancyStatus] to apply to the query criteria.
     */
    @QueryDslContext
    fun relevancyStatus(value: Vault.RelevancyStatus) {
        commonQueryCriteria.relevancyStatus = value
        criteria = criteria.updateQueryCriteria()
    }

    /**
     * Specifies the state status of the query criteria.
     *
     * @param value The [Vault.StateStatus] to apply to the query criteria.
     */
    @QueryDslContext
    fun stateStatus(value: Vault.StateStatus) {
        commonQueryCriteria.status = value
        criteria = criteria.updateQueryCriteria()
    }

    /**
     * Specifies a custom query criteria expression to apply to the query criteria.
     *
     * @param value The custom query criteria expression to apply to the query criteria.
     */
    @QueryDslContext
    fun <T : StatePersistable, P> expression(value: ColumnPredicateExpression<T, P>) {
        val criteriaToAdd = VaultCustomQueryCriteria(value)
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the external identifiers to apply to the query criteria.
     *
     * @param values The [String] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun externalIds(values: Iterable<String>?) {
        val criteriaToAdd = LinearStateQueryCriteria(externalId = values?.toList())
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the external identifiers to apply to the query criteria.
     *
     * @param values The [String] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun externalIds(vararg values: String) {
        externalIds(values.toList())
    }

    /**
     * Specifies the unique identifiers to apply to the query criteria.
     *
     * @param values The [UniqueIdentifier] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun linearIds(values: Iterable<UniqueIdentifier>?) {
        val criteriaToAdd = LinearStateQueryCriteria(linearId = values?.toList())
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the unique identifiers to apply to the query criteria.
     *
     * @param values The [UniqueIdentifier] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun linearIds(vararg values: UniqueIdentifier) {
        linearIds(values.toList())
    }

    /**
     * Specifies the issuers of [FungibleAsset] states to apply to the query criteria.
     *
     * @param values The [AbstractParty] issuers of [FungibleAsset] states to apply to the query criteria.
     */
    @QueryDslContext
    fun issuers(values: Iterable<AbstractParty>?) {
        val criteriaToAdd = FungibleAssetQueryCriteria(issuer = values?.toList())
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the issuers of [FungibleAsset] states to apply to the query criteria.
     *
     * @param values The [AbstractParty] issuers of [FungibleAsset] states to apply to the query criteria.
     */
    @QueryDslContext
    fun issuers(vararg values: AbstractParty) {
        issuers(values.toList())
    }

    /**
     * Specifies the issuer refs of [FungibleAsset] states to apply to the query criteria.
     *
     * @param values The [AbstractParty] issuer refs of [FungibleAsset] states to apply to the query criteria.
     */
    @QueryDslContext
    fun issuerRefs(values: Iterable<OpaqueBytes>?) {
        val criteriaToAdd = FungibleAssetQueryCriteria(issuerRef = values?.toList())
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the issuer refs of [FungibleAsset] states to apply to the query criteria.
     *
     * @param values The [AbstractParty] issuer refs of [FungibleAsset] states to apply to the query criteria.
     */
    @QueryDslContext
    fun issuerRefs(vararg values: OpaqueBytes) {
        issuerRefs(values.toList())
    }

    /**
     * Specifies the notaries to apply to the query criteria.
     *
     * @param values The notary [AbstractParty] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun notaries(values: Iterable<AbstractParty>?) {
        val criteriaToAdd = VaultQueryCriteria(notary = values?.toList())
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the notaries to apply to the query criteria.
     *
     * @param values The notary [AbstractParty] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun notaries(vararg values: AbstractParty) {
        notaries(values.toList())
    }

    /**
     * Specifies the owners of [FungibleAsset] states to apply to the query criteria.
     *
     * @param values The [AbstractParty] owners of [FungibleAsset] states to apply to the query criteria.
     */
    @QueryDslContext
    fun owners(values: Iterable<AbstractParty>?) {
        val criteriaToAdd = FungibleAssetQueryCriteria(owner = values?.toList())
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the owners of [FungibleAsset] states to apply to the query criteria.
     *
     * @param values The [AbstractParty] owners of [FungibleAsset] states to apply to the query criteria.
     */
    @QueryDslContext
    fun owners(vararg values: AbstractParty) {
        owners(values.toList())
    }

    /**
     * Specifies a column predicate that determines the quantity of a [FungibleAsset] to apply to the query criteria.
     *
     * @param value The [ColumnPredicate] that determines the quantity of a [FungibleAsset] to apply to the query criteria.
     */
    @QueryDslContext
    fun fungibleAssetQuantity(value: ColumnPredicate<Long>?) {
        val criteriaToAdd = FungibleAssetQueryCriteria(quantity = value)
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies a column predicate that determines the quantity of a [FungibleState] to apply to the query criteria.
     *
     * @param value The [ColumnPredicate] that determines the quantity of a [FungibleState] to apply to the query criteria.
     */
    @QueryDslContext
    fun fungibleStateQuantity(value: ColumnPredicate<Long>?) {
        val criteriaToAdd = FungibleStateQueryCriteria(quantity = value)
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the soft locking condition of the query criteria.
     *
     * @param value The [SoftLockingCondition] to apply to the query criteria.
     */
    @QueryDslContext
    fun softLockingCondition(value: SoftLockingCondition?) {
        val criteriaToAdd = VaultQueryCriteria(softLockingCondition = value)
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the state references to apply to the query criteria.
     *
     * @param values The [StateRef] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun stateRefs(values: Iterable<StateRef>?) {
        val criteriaToAdd = VaultQueryCriteria(stateRefs = values?.toList())
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Specifies the state references to apply to the query criteria.
     *
     * @param values The [StateRef] instances to apply to the query criteria.
     */
    @QueryDslContext
    fun stateRefs(vararg values: StateRef) {
        stateRefs(values.toList())
    }

    /**
     * Specifies the time condition of the query criteria.
     *
     * @param value The [TimeCondition] to apply to the query criteria.
     */
    @QueryDslContext
    fun timeCondition(value: TimeCondition?) {
        val criteriaToAdd = VaultQueryCriteria(timeCondition = value)
        criteria = criteria.and(criteriaToAdd.updateQueryCriteria())
    }

    /**
     * Applies a sub-query to the current query criteria using logical AND.
     *
     * @param queryCriteria The sub-query criteria which will be appended to the current query criteria.
     */
    @QueryDslContext
    fun and(queryCriteria: QueryCriteria) {
        criteria = criteria.and(queryCriteria)
    }

    /**
     * Applies a sub-query to the current query criteria using logical AND.
     *
     * @param action The [QueryDsl] action which will be used to build the sub-query.
     */
    @QueryDslContext
    fun and(action: QueryDsl<T>.() -> Unit) {
        val query = QueryDsl<T>(VaultQueryCriteria().updateQueryCriteria(), paging, sorting)
        action(query)
        and(query.criteria)
    }

    /**
     * Applies a sub-query to the current query criteria using logical OR.
     *
     * @param queryCriteria The sub-query criteria which will be appended to the current query criteria.
     */
    @QueryDslContext
    fun or(queryCriteria: QueryCriteria) {
        criteria = criteria.or(queryCriteria)
    }

    /**
     * Applies a sub-query to the current query criteria using logical OR.
     *
     * @param action The [QueryDsl] action which will be used to build the sub-query.
     */
    @QueryDslContext
    fun or(action: QueryDsl<T>.() -> Unit) {
        val query = QueryDsl<T>(VaultQueryCriteria().updateQueryCriteria(), paging, sorting)
        action(query)
        or(query.criteria)
    }

    /**
     * Specifies the page specification which will be applied to the query.
     *
     * @param value The [PageSpecification] to apply to the query.
     */
    @QueryDslContext
    fun page(value: PageSpecification) {
        paging = value
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
     * @param value The [Sort] to apply to the query.
     */
    @QueryDslContext
    fun sort(value: Sort) {
        sorting = value
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

    /**
     * Gets the fully built query criteria expression.
     *
     * @return Returns a [QueryCriteria] representing the vault query.
     */
    fun getQueryCriteria(): QueryCriteria {
        return criteria
    }

    /**
     * Gets the paging specification for this query.
     *
     * @return Returns a [PageSpecification] for this query.
     */
    fun getPaging(): PageSpecification {
        return paging
    }

    /**
     * Gets the sorting for this query.
     *
     * @return Returns a [Sort] for this query.
     */
    fun getSorting(): Sort {
        return sorting
    }

    /**
     * Updates the current [QueryCriteria] with the values maintained in the common query criteria object.
     *
     * @return Returns the updated query criteria.
     */
    private fun QueryCriteria.updateQueryCriteria(): QueryCriteria = when (this) {
        is VaultQueryCriteria -> updateQueryCriteria()
        is VaultCustomQueryCriteria<*> -> updateQueryCriteria()
        is LinearStateQueryCriteria -> updateQueryCriteria()
        is FungibleAssetQueryCriteria -> updateQueryCriteria()
        is FungibleStateQueryCriteria -> updateQueryCriteria()
        else -> this
    }

    /**
     * Updates the current [VaultQueryCriteria] with the values maintained in the common query criteria object.
     *
     * @return Returns the updated query criteria.
     */
    private fun VaultQueryCriteria.updateQueryCriteria(): VaultQueryCriteria = copy(
        constraintTypes = commonQueryCriteria.constraintTypes,
        constraints = commonQueryCriteria.constraints,
        contractStateTypes = commonQueryCriteria.contractStateTypes,
        exactParticipants = commonQueryCriteria.exactParticipants,
        externalIds = commonQueryCriteria.externalIds,
        participants = commonQueryCriteria.participants,
        relevancyStatus = commonQueryCriteria.relevancyStatus,
        status = commonQueryCriteria.status
    )

    /**
     * Updates the current [VaultCustomQueryCriteria] with the values maintained in the common query criteria object.
     *
     * @return Returns the updated query criteria.
     */
    private fun VaultCustomQueryCriteria<*>.updateQueryCriteria(): VaultCustomQueryCriteria<*> = copy(
        contractStateTypes = commonQueryCriteria.contractStateTypes,
        relevancyStatus = commonQueryCriteria.relevancyStatus,
        status = commonQueryCriteria.status
    )

    /**
     * Updates the current [LinearStateQueryCriteria] with the values maintained in the common query criteria object.
     *
     * @return Returns the updated query criteria.
     */
    private fun LinearStateQueryCriteria.updateQueryCriteria(): LinearStateQueryCriteria = copy(
        contractStateTypes = commonQueryCriteria.contractStateTypes,
        participants = commonQueryCriteria.participants,
        relevancyStatus = commonQueryCriteria.relevancyStatus,
        status = commonQueryCriteria.status
    )

    /**
     * Updates the current [FungibleAssetQueryCriteria] with the values maintained in the common query criteria object.
     *
     * @return Returns the updated query criteria.
     */
    private fun FungibleAssetQueryCriteria.updateQueryCriteria(): FungibleAssetQueryCriteria = copy(
        contractStateTypes = commonQueryCriteria.contractStateTypes,
        participants = commonQueryCriteria.participants,
        relevancyStatus = commonQueryCriteria.relevancyStatus,
        status = commonQueryCriteria.status
    )

    /**
     * Updates the current [FungibleStateQueryCriteria] with the values maintained in the common query criteria object.
     *
     * @return Returns the updated query criteria.
     */
    private fun FungibleStateQueryCriteria.updateQueryCriteria(): FungibleStateQueryCriteria = copy(
        contractStateTypes = commonQueryCriteria.contractStateTypes,
        participants = commonQueryCriteria.participants,
        relevancyStatus = commonQueryCriteria.relevancyStatus,
        status = commonQueryCriteria.status
    )
}
