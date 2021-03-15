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

package io.onixlabs.corda.core

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Represents a type reference which obtains full generic type information for the underlying generic type.
 *
 * This implementation is inspired by the TypeReference class in fasterxml, jackson-core.
 * https://fasterxml.github.io/jackson-core/javadoc/2.8/com/fasterxml/jackson/core/type/TypeReference.html
 *
 * @param T The underlying type for which to obtain type information.
 * @property type The actual underlying type.
 * @property arguments The actual generic argument types of the underlying type.
 */
abstract class TypeReference<T> : Comparable<TypeReference<T>> {

    val type: Type get() = getGenericType()
    val arguments: List<Type> get() = getTypeArguments()

    /**
     * The only reason this method, and subsequent implementation of [Comparable] is defined
     * is to prevent constructing a type reference without type information.
     */
    final override fun compareTo(other: TypeReference<T>): Int {
        return 0
    }

    /**
     * Gets the underlying generic type.
     *
     * @return Returns the underlying type [T].
     * @throws IllegalStateException if the type reference is constructed without type information.
     */
    private fun getGenericType(): Type {
        val superClass = javaClass.genericSuperclass

        check(superClass !is Class<*>) {
            "TypeReference constructed without actual type information."
        }

        return (superClass as ParameterizedType).actualTypeArguments[0]
    }

    /**
     * Gets a list of generic type arguments for the generic type.
     *
     * @return Returns a list of generic type arguments for the generic type.
     */
    private fun getTypeArguments(): List<Type> {
        val type = getGenericType()
        return if (type is ParameterizedType) {
            type.actualTypeArguments.toList()
        } else emptyList()
    }
}
