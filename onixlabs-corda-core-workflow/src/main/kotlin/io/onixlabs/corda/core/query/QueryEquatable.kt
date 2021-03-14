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

import net.corda.core.node.services.vault.BinaryLogicalOperator
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.Builder.isNull
import net.corda.core.node.services.vault.Builder.notEqual
import net.corda.core.node.services.vault.Builder.notNull
import net.corda.core.node.services.vault.CriteriaExpression
import net.corda.core.serialization.CordaSerializable
import kotlin.reflect.KProperty1

/**
 * Represents the base class for implementing equatable query parameters.
 *
 * @param T The underlying value type.
 * @property value The value specified for the query.
 */
@CordaSerializable
sealed class QueryEquatable<T> {

    companion object {

        /**
         * Creates an "equal to" equatable query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @param ignoreCase Determines whether to ignore case when performing queries.
         * @return Returns a [QueryComparable] representing the equatable query.
         */
        fun <T> equalTo(value: T, ignoreCase: Boolean = false): QueryEquatable<T> {
            return EqualToQueryEquatable(value, ignoreCase)
        }

        /**
         * Creates a "not equal to" equatable query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @param ignoreCase Determines whether to ignore case when performing queries.
         * @return Returns a [QueryComparable] representing the equatable query.
         */
        fun <T> notEqualTo(value: T, ignoreCase: Boolean = false): QueryEquatable<T> {
            return NotEqualToQueryEquatable(value, ignoreCase)
        }
    }

    abstract val value: T

    /**
     * Creates a criteria expression.
     *
     * @param R The underlying type of the property receiver.
     * @param P The underlying type of the property.
     * @param property The property from which to create a criteria expression.
     * @param projection Performs a projection from the specified value to the query value.
     * @return Returns a [CriteriaExpression] representing the query expression to perform.
     */
    abstract fun <R, P> toExpression(
        property: KProperty1<R, P>,
        projection: (T) -> P
    ): CriteriaExpression<R, Boolean>

    /**
     * Represents an "equal to" equatable query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     */
    private class EqualToQueryEquatable<T>(
        override val value: T,
        private val ignoreCase: Boolean = false
    ) : QueryEquatable<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P
        ): CriteriaExpression<R, Boolean> {
            val projectedValue = projection(value)
            return if (projectedValue == null) {
                property.isNull()
            } else {
                property.equal(projectedValue, !ignoreCase)
            }
        }
    }

    /**
     * Represents a "not equal to" equatable query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     */
    private class NotEqualToQueryEquatable<T>(
        override val value: T,
        private val ignoreCase: Boolean = false
    ) : QueryEquatable<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P
        ): CriteriaExpression<R, Boolean> {
            val projectedValue = projection(value)
            return if (projectedValue == null) {
                property.notNull()
            } else {
                CriteriaExpression.BinaryLogical(
                    left = property.notEqual(projectedValue, !ignoreCase),
                    right = property.isNull(),
                    operator = BinaryLogicalOperator.OR
                )
            }
        }
    }
}
