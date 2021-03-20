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

package io.onixlabs.corda.test

import io.onixlabs.corda.core.typeInfo
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class TypeInfoTests {

    @Test
    fun `TypeInfo should correctly identify the Int type`() {

        // Arrange
        val expected = Integer::class.java
        val typeInfo = typeInfo<Int>()

        // Act
        val actual = typeInfo.typeClass as Class<*>

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `TypeInfo should correctly identify the String type`() {

        // Arrange
        val expected = String::class.java
        val typeInfo = typeInfo<String>()

        // Act
        val actual = typeInfo.typeClass as Class<*>

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `TypeInfo should correctly identify the BigDecimal type`() {

        // Arrange
        val expected = BigDecimal::class.java
        val typeInfo = typeInfo<BigDecimal>()

        // Act
        val actual = typeInfo.typeClass as Class<*>

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `TypeInfo should correctly identify the List of Int type`() {

        // Arrange
        val expected = List::class.java
        val expectedParameter1 = typeInfo<Int>()
        val typeInfo = typeInfo<List<Int>>()

        // Act
        val actual = typeInfo.typeClass as Class<*>
        val actualParameter1 = typeInfo.typeArguments.single()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
    }

    @Test
    fun `TypeInfo should correctly identify the List of String type`() {

        // Arrange
        val expected = List::class.java
        val expectedParameter1 = typeInfo<String>()
        val typeInfo = typeInfo<List<String>>()

        // Act
        val actual = typeInfo.typeClass as Class<*>
        val actualParameter1 = typeInfo.typeArguments.single()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
    }

    @Test
    fun `TypeInfo should correctly identify the List of BigDecimal type`() {

        // Arrange
        val expected = List::class.java
        val expectedParameter1 = typeInfo<BigDecimal>()
        val typeInfo = typeInfo<List<BigDecimal>>()

        // Act
        val actual = typeInfo.typeClass as Class<*>
        val actualParameter1 = typeInfo.typeArguments.single()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
    }

    @Test
    fun `TypeInfo should correctly identify the Map of String to Int type`() {

        // Arrange
        val expected = Map::class.java
        val expectedParameter1 = typeInfo<String>()
        val expectedParameter2 = typeInfo<Int>()
        val typeInfo = typeInfo<Map<String, Int>>()

        // Act
        val actual = typeInfo.typeClass as Class<*>
        val actualParameter1 = typeInfo.typeArguments.first()
        val actualParameter2 = typeInfo.typeArguments.last()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
        assertEquals(expectedParameter2, actualParameter2)
    }

    @Test
    fun `TypeInfo should correctly identify the List of wildcard type`() {

        // Arrange
        val expected = List::class.java
        val expectedParameter1 = typeInfo<Any>()
        val typeInfo = typeInfo<List<*>>()

        // Act
        val actual = typeInfo.typeClass as Class<*>
        val actualParameter1 = typeInfo.typeArguments.first()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
    }
}
