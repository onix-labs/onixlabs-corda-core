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
import net.corda.core.node.services.vault.Builder.`in`
import net.corda.core.node.services.vault.Builder.between
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.Builder.greaterThan
import net.corda.core.node.services.vault.Builder.greaterThanOrEqual
import net.corda.core.node.services.vault.Builder.isNull
import net.corda.core.node.services.vault.Builder.lessThan
import net.corda.core.node.services.vault.Builder.lessThanOrEqual
import net.corda.core.node.services.vault.Builder.notEqual
import net.corda.core.node.services.vault.Builder.notIn
import net.corda.core.node.services.vault.Builder.notNull
import net.corda.core.node.services.vault.CriteriaExpression
import net.corda.core.serialization.CordaSerializable
import kotlin.reflect.KProperty1

/**
 * Represents the base class for implementing comparable query parameters.
 *
 * @param T The underlying value type.
 * @property value The value specified for the query.
 */
@CordaSerializable
sealed class QueryComparable<T> {

    companion object {

        /**
         * Creates an "equal to" comparable query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @param ignoreCase Determines whether to ignore case when performing queries.
         * @return Returns a [QueryComparable] representing the comparable query.
         */
        fun <T> equalTo(value: T, ignoreCase: Boolean = false): QueryComparable<T> {
            return EqualToQueryComparable(value, ignoreCase)
        }

        /**
         * Creates a "not equal to" comparable query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @param ignoreCase Determines whether to ignore case when performing queries.
         * @return Returns a [QueryComparable] representing the comparable query.
         */
        fun <T> notEqualTo(value: T, ignoreCase: Boolean = false): QueryComparable<T> {
            return NotEqualToQueryComparable(value, ignoreCase)
        }

        /**
         * Creates a "greater than" comparable query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @return Returns a [QueryComparable] representing the comparable query.
         */
        fun <T> greaterThan(value: T): QueryComparable<T> {
            return GreaterThanQueryComparable(value)
        }

        /**
         * Creates a "greater than or equal to" comparable query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @return Returns a [QueryComparable] representing the comparable query.
         */
        fun <T> greaterThanOrEqualTo(value: T): QueryComparable<T> {
            return GreaterThanOrEqualToQueryComparable(value)
        }

        /**
         * Creates a "less than" comparable query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @return Returns a [QueryComparable] representing the comparable query.
         */
        fun <T> lessThan(value: T): QueryComparable<T> {
            return LessThanQueryComparable(value)
        }

        /**
         * Creates a "less than or equal to" comparable query.
         *
         * @param T The underlying type of the specified query value.
         * @param value The specified value to query.
         * @return Returns a [QueryComparable] representing the comparable query.
         */
        fun <T> lessThanOrEqualTo(value: T): QueryComparable<T> {
            return LessThanOrEqualToQueryComparable(value)
        }

        /**
         * Creates a "between" comparable query.
         *
         * @param T The underlying type of the specified query value.
         * @param range The range of values that the underlying value must be between.
         * @return Returns a [QueryComparable] representing the comparable query.
         */
        fun <T : Comparable<T>> between(range: ClosedRange<T>): QueryComparable<T> {
            return BetweenQueryComparable(range)
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
            return WithinQueryComparable(items, ignoreCase)
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
            return NotWithinQueryComparable(items, ignoreCase)
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
    abstract fun <R, P : Comparable<P>> toExpression(
        property: KProperty1<R, P>,
        projection: (T) -> P?
    ): CriteriaExpression<R, Boolean>

    /**
     * Projects the specified value, or throws an exception if the projection result is null.
     *
     * @param P The underlying result type of the projection.
     * @param projection The projection function to project the value.
     * @return Returns the projected value.
     * @throws IllegalArgumentException if the projected value is null.
     */
    protected fun <P> projectOrThrow(projection: (T) -> P?, value: T, operation: String): P {
        return projection(value) ?: throw IllegalArgumentException("Non-null value expected for $operation expression.")
    }

    /**
     * Represents an "equal to" comparable query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     */
    private class EqualToQueryComparable<T>(
        override val value: T,
        private val ignoreCase: Boolean = false
    ) : QueryComparable<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
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
     * Represents a "not equal to" comparable query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     */
    private class NotEqualToQueryComparable<T>(
        override val value: T,
        private val ignoreCase: Boolean = false
    ) : QueryComparable<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
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
     * Represents a "greater than" comparable query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     */
    private class GreaterThanQueryComparable<T>(
        override val value: T
    ) : QueryComparable<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
        ): CriteriaExpression<R, Boolean> {
            val projectedValue = projectOrThrow(projection, value, "greaterThan")
            return property.greaterThan(projectedValue)
        }
    }

    /**
     * Represents a "greater than or equal to" comparable query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     */
    private class GreaterThanOrEqualToQueryComparable<T>(
        override val value: T
    ) : QueryComparable<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
        ): CriteriaExpression<R, Boolean> {
            val projectedValue = projectOrThrow(projection, value, "greaterThanOrEqualTo")
            return property.greaterThanOrEqual(projectedValue)
        }
    }

    /**
     * Represents a "less than" comparable query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     */
    private class LessThanQueryComparable<T>(
        override val value: T
    ) : QueryComparable<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
        ): CriteriaExpression<R, Boolean> {
            val projectedValue = projectOrThrow(projection, value, "lessThan")
            return property.lessThan(projectedValue)
        }
    }

    /**
     * Represents a "less than or equal to" comparable query.
     *
     * @param T The underlying value type.
     * @property value The value specified for the query.
     */
    private class LessThanOrEqualToQueryComparable<T>(
        override val value: T
    ) : QueryComparable<T>() {

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
        ): CriteriaExpression<R, Boolean> {
            val projectedValue = projectOrThrow(projection, value, "lessThanOrEqualTo")
            return property.lessThanOrEqual(projectedValue)
        }
    }

    /**
     * Represents a "between" comparable query.
     *
     * @param T The underlying value type.
     * @property range The range of values that the underlying value must be between.
     * @property value The value property is not usable in this context. Use the range property instead.
     */
    private class BetweenQueryComparable<T : Comparable<T>>(
        val range: ClosedRange<T>
    ) : QueryComparable<T>() {

        override val value: T
            get() = throw IllegalStateException("Use 'range' instead of 'value' for '${javaClass.canonicalName}'.")

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
        ): CriteriaExpression<R, Boolean> {
            val start = projectOrThrow(projection, range.start, "between")
            val end = projectOrThrow(projection, range.endInclusive, "between")
            return property.between(start, end)
        }
    }

    /**
     * Represents a "within" (or "in") comparable query.
     *
     * @param T The underlying value type.
     * @property items The items that the underlying value must be within.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     * @property value The value property is not usable in this context. Use the items property instead.
     */
    private class WithinQueryComparable<T : Comparable<T>>(
        val items: Iterable<T>,
        private val ignoreCase: Boolean = false
    ) : QueryComparable<T>() {

        override val value: T
            get() = throw IllegalStateException("Use 'items' instead of 'value' for '${javaClass.canonicalName}'.")

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
        ): CriteriaExpression<R, Boolean> {
            val projectedItems = items.map { projectOrThrow(projection, it, "within") }
            return property.`in`(projectedItems, !ignoreCase)
        }
    }

    /**
     * Represents a "not within" (or "not in") comparable query.
     *
     * @param T The underlying value type.
     * @property items The items that the underlying value must not be within.
     * @param ignoreCase Determines whether to ignore case when performing queries.
     * @property value The value property is not usable in this context. Use the items property instead.
     */
    private class NotWithinQueryComparable<T : Comparable<T>>(
        val items: Iterable<T>,
        private val ignoreCase: Boolean = false
    ) : QueryComparable<T>() {

        override val value: T
            get() = throw IllegalStateException("Use 'items' instead of 'value' for '${javaClass.canonicalName}'.")

        /**
         * Creates a criteria expression.
         *
         * @param R The underlying type of the property receiver.
         * @param P The underlying type of the property.
         * @param property The property from which to create a criteria expression.
         * @param projection Performs a projection from the specified value to the query value.
         * @return Returns a [CriteriaExpression] representing the query expression to perform.
         */
        override fun <R, P : Comparable<P>> toExpression(
            property: KProperty1<R, P>,
            projection: (T) -> P?
        ): CriteriaExpression<R, Boolean> {
            val projectedItems = items.map { projectOrThrow(projection, it, "not within") }
            return property.notIn(projectedItems, !ignoreCase)
        }
    }
}
