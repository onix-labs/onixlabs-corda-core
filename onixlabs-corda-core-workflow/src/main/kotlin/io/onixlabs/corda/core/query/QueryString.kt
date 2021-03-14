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
import net.corda.core.node.services.vault.Builder.like
import net.corda.core.node.services.vault.Builder.notEqual
import net.corda.core.node.services.vault.Builder.notLike
import net.corda.core.node.services.vault.Builder.notNull
import net.corda.core.node.services.vault.CriteriaExpression
import net.corda.core.serialization.CordaSerializable
import kotlin.reflect.KProperty1

/**
 * Represents the base class for implementing string query parameters.
 *
 * @param T The underlying value type.
 * @property value The value specified for the query.
 */
@CordaSerializable
sealed class QueryString<T> {

    companion object {

        /**
         * Creates an "equal to" string query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @param ignoreCase Determines whether to ignore case when performing queries.
         * @return Returns a [QueryComparable] representing the equatable query.
         */
        fun <T> equalTo(value: T, ignoreCase: Boolean = false): QueryString<T> {
            return EqualToQueryString(value, ignoreCase)
        }

        /**
         * Creates a "not equal to" string query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @param ignoreCase Determines whether to ignore case when performing queries.
         * @return Returns a [QueryComparable] representing the equatable query.
         */
        fun <T> notEqualTo(value: T, ignoreCase: Boolean = false): QueryString<T> {
            return NotEqualToQueryString(value, ignoreCase)
        }

        /**
         * Creates a "like" string query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @param ignoreCase Determines whether to ignore case when performing queries.
         * @return Returns a [QueryComparable] representing the equatable query.
         */
        fun <T> like(value: T, ignoreCase: Boolean = false): QueryString<T> {
            return LikeQueryString(value, ignoreCase)
        }

        /**
         * Creates a "not like" string query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @param ignoreCase Determines whether to ignore case when performing queries.
         * @return Returns a [QueryComparable] representing the equatable query.
         */
        fun <T> notLike(value: T, ignoreCase: Boolean = false): QueryString<T> {
            return NotLikeQueryString(value, ignoreCase)
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
    abstract fun <R> toExpression(
        property: KProperty1<R, String?>,
        projection: (T) -> String?
    ): CriteriaExpression<R, Boolean>

    /**
     * Projects the specified value, or throws an exception if the projection result is null.
     *
     * @param P The underlying result type of the projection.
     * @param projection The projection function to project the value.
     * @return Returns the projected value.
     * @throws IllegalArgumentException if the projected value is null.
     */
    protected fun projectOrThrow(projection: (T) -> String?, value: T, operation: String): String {
        return projection(value) ?: throw IllegalArgumentException("Non-null value expected for $operation expression.")
    }

    /**
     * Represents an "equal to" string query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     */
    private class EqualToQueryString<T>(
        override val value: T,
        private val ignoreCase: Boolean = false
    ) : QueryString<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R> toExpression(
            property: KProperty1<R, String?>,
            projection: (T) -> String?
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
     * Represents a "not equal to" string query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     */
    private class NotEqualToQueryString<T>(
        override val value: T,
        private val ignoreCase: Boolean = false
    ) : QueryString<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R> toExpression(
            property: KProperty1<R, String?>,
            projection: (T) -> String?
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

    /**
     * Represents a "like" string query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     */
    private class LikeQueryString<T>(
        override val value: T,
        private val ignoreCase: Boolean = false
    ) : QueryString<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R> toExpression(
            property: KProperty1<R, String?>,
            projection: (T) -> String?
        ): CriteriaExpression<R, Boolean> {
            val projectedValue = projectOrThrow(projection, value, "like")
            return property.like(projectedValue, !ignoreCase)
        }
    }

    /**
     * Represents a "not like" string query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     */
    private class NotLikeQueryString<T>(
        override val value: T,
        private val ignoreCase: Boolean = false
    ) : QueryString<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R> toExpression(
            property: KProperty1<R, String?>,
            projection: (T) -> String?
        ): CriteriaExpression<R, Boolean> {
            val projectedValue = projectOrThrow(projection, value, "not like")
            return property.notLike(projectedValue, !ignoreCase)
        }
    }
}
