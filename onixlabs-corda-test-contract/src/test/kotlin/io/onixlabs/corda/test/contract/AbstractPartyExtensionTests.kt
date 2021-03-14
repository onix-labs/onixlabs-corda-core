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

import io.onixlabs.corda.core.contract.owningKeys
import io.onixlabs.corda.core.contract.participantHash
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AbstractPartyExtensionTests {

    @Test
    fun `owningKeys should contain the owning keys of the initial collection of participants`() {

        // Arrange
        val participants = listOf(IDENTITY_A.party, IDENTITY_B.party, IDENTITY_C.party)

        // Act
        val owningKeys = participants.owningKeys

        // Assert
        assert(IDENTITY_A.publicKey in owningKeys)
        assert(IDENTITY_B.publicKey in owningKeys)
        assert(IDENTITY_C.publicKey in owningKeys)
    }

    @Test
    fun `participantHash should produce the same hash for different ordered participant collections`() {

        // Arrange
        val participantsA = listOf(IDENTITY_A.party, IDENTITY_B.party, IDENTITY_C.party)
        val participantsB = listOf(IDENTITY_B.party, IDENTITY_C.party, IDENTITY_A.party)
        val participantsC = listOf(IDENTITY_C.party, IDENTITY_B.party, IDENTITY_A.party)

        // Act
        val participantHashA = participantsA.participantHash
        val participantHashB = participantsB.participantHash
        val participantHashC = participantsC.participantHash

        // Assert
        assertEquals(participantHashA, participantHashB)
        assertEquals(participantHashA, participantHashC)
    }
}
