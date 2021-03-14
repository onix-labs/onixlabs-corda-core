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

package io.onixlabs.corda.core.query

/**
 * Creates an "equal to" comparable query.
 *
 * @param T The underlying type of the specified query value.
 * @param value The specified value to query.
 * @param ignoreCase Determines whether to ignore case when performing queries.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T> comparableEqualTo(value: T, ignoreCase: Boolean = false): QueryComparable<T> {
    return QueryComparable.equalTo(value, ignoreCase)
}

/**
 * Creates a "not equal to" comparable query.
 *
 * @param T The underlying type of the specified query value.
 * @param value The specified value to query.
 * @param ignoreCase Determines whether to ignore case when performing queries.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T> comparableNotEqualTo(value: T, ignoreCase: Boolean = false): QueryComparable<T> {
    return QueryComparable.notEqualTo(value, ignoreCase)
}

/**
 * Creates a "greater than" comparable query.
 *
 * @param T The underlying type of the specified query value.
 * @param value The specified value to query.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T> greaterThan(value: T): QueryComparable<T> {
    return QueryComparable.greaterThan(value)
}

/**
 * Creates a "greater than or equal to" comparable query.
 *
 * @param T The underlying type of the specified query value.
 * @param value The specified value to query.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T> greaterThanOrEqualTo(value: T): QueryComparable<T> {
    return QueryComparable.greaterThanOrEqualTo(value)
}

/**
 * Creates a "less than" comparable query.
 *
 * @param T The underlying type of the specified query value.
 * @param value The specified value to query.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T> lessThan(value: T): QueryComparable<T> {
    return QueryComparable.lessThan(value)
}

/**
 * Creates a "less than or equal to" comparable query.
 *
 * @param T The underlying type of the specified query value.
 * @param value The specified value to query.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T> lessThanOrEqualTo(value: T): QueryComparable<T> {
    return QueryComparable.lessThanOrEqualTo(value)
}

/**
 * Creates a "between" comparable query.
 *
 * @param T The underlying type of the specified query value.
 * @param range The range of values that the underlying value must be between.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T : Comparable<T>> between(range: ClosedRange<T>): QueryComparable<T> {
    return QueryComparable.between(range)
}

/**
 * Creates a "within" (or "in") comparable query.
 *
 * @param T The underlying value type.
 * @param items The items that the underlying value must be within.
 * @param ignoreCase Determines whether to ignore case when performing queries.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T : Comparable<T>> within(items: Iterable<T>, ignoreCase: Boolean = false): QueryComparable<T> {
    return QueryComparable.within(items, ignoreCase)
}

/**
 * Creates a "not within" (or "not in") comparable query.
 *
 * @param T The underlying value type.
 * @param items The items that the underlying value must not be within.
 * @param ignoreCase Determines whether to ignore case when performing queries.
 * @return Returns a [QueryComparable] representing the comparable query.
 */
fun <T : Comparable<T>> notWithin(items: Iterable<T>, ignoreCase: Boolean = false): QueryComparable<T> {
    return QueryComparable.notWithin(items, ignoreCase)
}
