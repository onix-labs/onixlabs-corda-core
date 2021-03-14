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

import io.onixlabs.corda.core.TypeReference
import io.onixlabs.corda.core.toClass
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class TypeReferenceTests {

    @Test
    fun `TypeReference should correctly identify the Int type`() {

        // Arrange
        val expected = Integer::class.java
        val typeReference = object : TypeReference<Int>() {}

        // Act
        val actual = typeReference.type.toClass()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `TypeReference should correctly identify the String type`() {

        // Arrange
        val expected = String::class.java
        val typeReference = object : TypeReference<String>() {}

        // Act
        val actual = typeReference.type.toClass()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `TypeReference should correctly identify the BigDecimal type`() {

        // Arrange
        val expected = BigDecimal::class.java
        val typeReference = object : TypeReference<BigDecimal>() {}

        // Act
        val actual = typeReference.type.toClass()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `TypeReference should correctly identify the List of Int type`() {

        // Arrange
        val expected = List::class.java
        val expectedParameter1 = Integer::class.java
        val typeReference = object : TypeReference<List<Int>>() {}

        // Act
        val actual = typeReference.type.toClass()
        val actualParameter1 = typeReference.arguments[0].toClass()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
    }

    @Test
    fun `TypeReference should correctly identify the List of String type`() {

        // Arrange
        val expected = List::class.java
        val expectedParameter1 = String::class.java
        val typeReference = object : TypeReference<List<String>>() {}

        // Act
        val actual = typeReference.type.toClass()
        val actualParameter1 = typeReference.arguments[0].toClass()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
    }

    @Test
    fun `TypeReference should correctly identify the List of BigDecimal type`() {

        // Arrange
        val expected = List::class.java
        val expectedParameter1 = BigDecimal::class.java
        val typeReference = object : TypeReference<List<BigDecimal>>() {}

        // Act
        val actual = typeReference.type.toClass()
        val actualParameter1 = typeReference.arguments[0].toClass()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
    }

    @Test
    fun `TypeReference should correctly identify the Map of String to Int type`() {

        // Arrange
        val expected = Map::class.java
        val expectedParameter1 = String::class.java
        val expectedParameter2 = Integer::class.java
        val typeReference = object : TypeReference<Map<String, Int>>() {}

        // Act
        val actual = typeReference.type.toClass()
        val actualParameter1 = typeReference.arguments[0].toClass()
        val actualParameter2 = typeReference.arguments[1].toClass()

        // Assert
        assertEquals(expected, actual)
        assertEquals(expectedParameter1, actualParameter1)
        assertEquals(expectedParameter2, actualParameter2)
    }
}
