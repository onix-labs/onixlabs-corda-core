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

package io.onixlabs.corda.core

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * Converts a type to a class.
 */
fun Type.toClass(): Class<*> = when (this) {
    is ParameterizedType -> rawType.toClass()
    is WildcardType -> this.upperBounds[0].toClass()
    is Class<*> -> this
    else -> Class.forName(typeName)
}

/**
 * Converts a type to a class.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Type.toTypedClass(): Class<T> {
    return toClass() as Class<T>
}
