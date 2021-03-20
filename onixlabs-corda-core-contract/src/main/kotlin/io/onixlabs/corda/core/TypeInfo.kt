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

import net.corda.core.serialization.CordaSerializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.*

/**
 * Represents a graph of type information about a generic type.
 *
 * @param T The underlying type of the type being represented.
 * @param typeClass The class of the underlying type.
 * @param typeArguments The arguments of the type, if the type is generic.
 */
@CordaSerializable
class TypeInfo<T> private constructor(val typeClass: Class<T>, val typeArguments: List<TypeInfo<*>>) {

    companion object {

        /**
         * Creates a [TypeInfo] instance from the specified [TypeReference].
         *
         * @param T The underlying type of the type being represented.
         * @param typeReference The [TypeReference] from which to obtain type information.
         * @return Returns a [TypeInfo] containing the type information created from the specified [TypeReference].
         */
        fun <T> fromTypeReference(typeReference: TypeReference<T>): TypeInfo<T> {
            return fromType(typeReference.type)
        }

        /**
         * Creates a [TypeInfo] instance from the specified reified type.
         * @param T The underlying type of the type being represented.
         * @return Returns a [TypeInfo] containing the type information created from the specified reified type.
         */
        inline fun <reified T> fromType(): TypeInfo<T> {
            return fromTypeReference(object : TypeReference<T>() {})
        }

        /**
         * Creates a [TypeInfo] instance from the specified [Type].
         *
         * @param T The underlying type of the type being represented.
         * @param type The [Type] from which to obtain type information.
         * @return Returns a [TypeInfo] containing the type information created from the specified [Type].
         */
        private fun <T> fromType(type: Type): TypeInfo<T> {

            fun getArgumentList(type: Type): List<Type> = when (type) {
                is ParameterizedType -> type.actualTypeArguments.toList()
                is WildcardType -> type.upperBounds.flatMap { getArgumentList(it) }
                else -> emptyList()
            }

            return TypeInfo(type.toTypedClass(), getArgumentList(type).map { fromType<Any>(it) })
        }
    }

    /**
     * Determines whether the specified object is equal to the current object.
     *
     * @param other The object to compare with the current object.
     * @return Returns true if the specified object is equal to the current object; otherwise, false.
     */
    override fun equals(other: Any?): Boolean {
        return this === other || (other is TypeInfo<*>
                && typeClass == other.typeClass
                && typeArguments == other.typeArguments
                && toString() == other.toString())
    }

    /**
     * Serves as the default hash function.
     *
     * @return Returns a hash code for the current object.
     */
    override fun hashCode(): Int {
        return Objects.hash(typeClass, typeArguments)
    }

    /**
     * Returns a string that represents the current object.
     *
     * @return Returns a string that represents the current object.
     */
    override fun toString(): String = buildString {
        append(typeClass.simpleName)
        if (typeArguments.isNotEmpty()) append("<${typeArguments.joinToString(", ")}>")
    }
}
