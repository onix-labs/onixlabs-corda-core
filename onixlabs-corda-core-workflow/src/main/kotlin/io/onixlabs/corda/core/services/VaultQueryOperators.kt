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

import net.corda.core.node.services.vault.Builder
import net.corda.core.node.services.vault.Builder.predicate
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria
import net.corda.core.schemas.StatePersistable
import kotlin.reflect.KProperty1

/**
 * Builds a query criterion for a property with null value.
 *
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
fun <T : StatePersistable, P> KProperty1<T, P?>.isNull(): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.isNull()))
}

/**
 * Builds a query criterion for a property with a non-null value.
 *
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
fun <T : StatePersistable, P> KProperty1<T, P?>.isNotNull(): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.isNotNull()))
}

/**
 * Builds a query criterion for a property with a value equal to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P> KProperty1<T, P?>.equalTo(value: P): QueryCriteria {
    return if (value == null) VaultCustomQueryCriteria(predicate(Builder.isNull()))
    else VaultCustomQueryCriteria(predicate(Builder.equal(value)))
}

/**
 * Builds a query criterion for a property with a value not equal to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P> KProperty1<T, P?>.notEqualTo(value: P): QueryCriteria {
    return if (value == null) VaultCustomQueryCriteria(predicate(Builder.isNotNull()))
    else VaultCustomQueryCriteria(predicate(Builder.notEqual(value)))
        .or(VaultCustomQueryCriteria(predicate(Builder.isNull())))
}

/**
 * Builds a query criterion for a property with a value greater than to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.greaterThan(value: P): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.greaterThan(value)))
}

/**
 * Builds a query criterion for a property with a value greater than or equal to to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.greaterThanOrEqualTo(value: P): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.greaterThanOrEqual(value)))
}

/**
 * Builds a query criterion for a property with a value less than the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.lessThan(value: P): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.lessThan(value)))
}

/**
 * Builds a query criterion for a property with a value less than or equal to the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.lessThanOrEqualTo(value: P): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.lessThanOrEqual(value)))
}

/**
 * Builds a query criterion for a property with a value between the specified minimum and maximum values.
 *
 * @param range The range value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.between(range: ClosedRange<P>): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.between(range.start, range.endInclusive)))
}

/**
 * Builds a query criterion for a property with a value like the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable> KProperty1<T, String?>.like(value: String): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.like(value)))
}

/**
 * Builds a query criterion for a property with a value not like the specified value.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable> KProperty1<T, String?>.notLike(value: String): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.notLike(value)))
}

/**
 * Builds a query criterion for a property with a value within the specified collection.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.within(value: Collection<P>): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.`in`(value)))
}

/**
 * Builds a query criterion for a property with a value not within the specified collection.
 *
 * @param value The value which will be used to build the query criterion.
 * @return Returns a [QueryCriteria] representing the specified custom query criterion.
 */
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.notWithin(value: Collection<P>): QueryCriteria {
    return VaultCustomQueryCriteria(predicate(Builder.notIn(value)))
}
