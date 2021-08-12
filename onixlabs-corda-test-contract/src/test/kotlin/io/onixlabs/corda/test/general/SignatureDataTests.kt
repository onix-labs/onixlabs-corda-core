/*
 * Copyright 2020-2021 ONIXLabs
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

package io.onixlabs.corda.test.general

import io.onixlabs.corda.core.contract.SignatureData
import io.onixlabs.corda.test.IDENTITY_A
import io.onixlabs.corda.test.IDENTITY_B
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
        val result = signatureData.isValid(IDENTITY_A.publicKey)

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
        val result = signatureData.isValid(IDENTITY_B.publicKey)

        // Assert
        assertFalse(result)
    }
}
