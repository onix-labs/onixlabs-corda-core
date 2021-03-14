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

package io.onixlabs.corda.test.contract

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ContractIDTests {

    @Test
    fun `CustomerContract ID should resolve to the correct canonical name`() {

        // Arrange
        val expected = "io.onixlabs.corda.test.contract.CustomerContract"

        // Act
        val actual = CustomerContract.ID

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `RewardContract ID should resolve to the correct canonical name`() {

        // Arrange
        val expected = "io.onixlabs.corda.test.contract.RewardContract"

        // Act
        val actual = RewardContract.ID

        // Assert
        assertEquals(expected, actual)
    }
}
