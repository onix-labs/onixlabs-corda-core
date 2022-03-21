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

import net.corda.core.node.services.vault.Builder
import net.corda.core.node.services.vault.Builder.predicate
import net.corda.core.node.services.vault.CriteriaExpression.ColumnPredicateExpression
import net.corda.core.schemas.StatePersistable
import kotlin.reflect.KProperty1

/**
 * Builds a column query expression for a property with null value.
 *
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
fun <T : StatePersistable, P> KProperty1<T, P?>.isNull()
        : ColumnPredicateExpression<T, P> = predicate(Builder.isNull())

/**
 * Builds a column query expression for a property with a non-null value.
 *
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
fun <T : StatePersistable, P> KProperty1<T, P?>.isNotNull()
        : ColumnPredicateExpression<T, P> = predicate(Builder.isNotNull())

/**
 * Builds a column query expression for a property with a value equal to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P> KProperty1<T, P?>.equalTo(value: P)
        : ColumnPredicateExpression<T, P> = predicate(Builder.equal(value))

/**
 * Builds a column query expression for a property with a value not equal to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P> KProperty1<T, P?>.notEqualTo(value: P)
        : ColumnPredicateExpression<T, P> = predicate(Builder.notEqual(value))

/**
 * Builds a column query expression for a property with a value greater than to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.greaterThan(value: P)
        : ColumnPredicateExpression<T, P> = predicate(Builder.greaterThan(value))

/**
 * Builds a column query expression for a property with a value greater than or equal to to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.greaterThanOrEqualTo(value: P)
        : ColumnPredicateExpression<T, P> = predicate(Builder.greaterThanOrEqual(value))

/**
 * Builds a column query expression for a property with a value less than the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.lessThan(value: P)
        : ColumnPredicateExpression<T, P> = predicate(Builder.lessThan(value))

/**
 * Builds a column query expression for a property with a value less than or equal to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.lessThanOrEqualTo(value: P)
        : ColumnPredicateExpression<T, P> = predicate(Builder.lessThanOrEqual(value))

/**
 * Builds a column query expression for a property with a value between the specified minimum and maximum values.
 *
 * @param range The range value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.between(range: ClosedRange<P>)
        : ColumnPredicateExpression<T, P> = predicate(Builder.between(range.start, range.endInclusive))

/**
 * Builds a column query expression for a property with a value like the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable> KProperty1<T, String?>.like(value: String)
        : ColumnPredicateExpression<T, String> = predicate(Builder.like(value))

/**
 * Builds a column query expression for a property with a value not like the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable> KProperty1<T, String?>.notLike(value: String)
        : ColumnPredicateExpression<T, String> = predicate(Builder.notLike(value))

/**
 * Builds a column query expression for a property with a value within the specified collection.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.within(value: Collection<P>)
        : ColumnPredicateExpression<T, P> = predicate(Builder.`in`(value))

/**
 * Builds a column query expression for a property with a value not within the specified collection.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [ColumnPredicateExpression] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.notWithin(value: Collection<P>)
        : ColumnPredicateExpression<T, P> = predicate(Builder.notIn(value))
