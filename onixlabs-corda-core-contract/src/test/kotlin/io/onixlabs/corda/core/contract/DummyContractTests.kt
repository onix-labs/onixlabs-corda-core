package io.onixlabs.corda.core.contract

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DummyContractTests {

    @Test
    fun `DummyContract ID should be the canonical name of the DummyContract class`() {

        // Arrange
        val expected = "io.onixlabs.corda.core.contract.DummyContract"

        // Act
        val actual = DummyContract.ID

        // Assert
        assertEquals(expected, actual)
    }
}
