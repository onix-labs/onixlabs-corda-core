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

import io.onixlabs.corda.core.contract.SignatureData
import net.corda.core.crypto.SecureHash
import net.corda.core.crypto.sign
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SignatureDataTests {

    @Test
    fun `verify of SignatureData should return true when verified by the correct public key`() {

        // Arrange
        val content = SecureHash.randomSHA256().bytes
        val signature = IDENTITY_A.keyPair.private.sign(content)
        val signatureData = SignatureData(content, signature)

        // Act
        val result = signatureData.verify(IDENTITY_A.publicKey)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `verify of SignatureData should return false when verified by the incorrect public key`() {

        // Arrange
        val content = SecureHash.randomSHA256().bytes
        val signature = IDENTITY_A.keyPair.private.sign(content)
        val signatureData = SignatureData(content, signature)

        // Act
        val result = signatureData.verify(IDENTITY_B.publicKey)

        // Assert
        assertFalse(result)
    }
}
