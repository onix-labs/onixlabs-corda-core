package io.onixlabs.corda.core.contract

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
