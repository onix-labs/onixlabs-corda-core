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
 * Creates an "equal to" equatable query.
 *
 * @param T The underlying type of the specified query value.
 * @param value The specified value to query.
 * @param ignoreCase Determines whether to ignore case when performing queries.
 * @return Returns a [QueryComparable] representing the equatable query.
 */
fun <T> equatableEqualTo(value: T, ignoreCase: Boolean = false): QueryEquatable<T> {
    return QueryEquatable.equalTo(value, ignoreCase)
}

/**
 * Creates a "not equal to" equatable query.
 *
 * @param T The underlying type of the specified query value.
 * @param value The specified value to query.
 * @param ignoreCase Determines whether to ignore case when performing queries.
 * @return Returns a [QueryComparable] representing the equatable query.
 */
fun <T> equatableNotEqualTo(value: T, ignoreCase: Boolean = false): QueryEquatable<T> {
    return QueryEquatable.notEqualTo(value, ignoreCase)
}
