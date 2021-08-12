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

import io.onixlabs.corda.core.contract.ChainState
import io.onixlabs.corda.core.contract.isPointingTo
import io.onixlabs.corda.test.EMPTY_STATE_REF
import io.onixlabs.corda.test.NOTARY
import io.onixlabs.corda.test.RANDOM_STATE_REF
import io.onixlabs.corda.test.contract.Customer
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.TransactionState
import net.corda.core.crypto.NullKeys.NULL_PARTY
import net.corda.core.internal.requiredContractClassName
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChainStateExtensionTests {

    @Test
    fun `isPointingTo should return true when the current ChainState is pointing to the specified StateAndRef`() {

        // Arrange
        val oldChainState = createStateAndRef(Customer(NULL_PARTY, "John", "Smith", Instant.MIN))
        val newChainState = oldChainState.state.data.copy(previousStateRef = oldChainState.ref)

        // Act
        val result = newChainState.isPointingTo(oldChainState)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `isPointingTo should return false when the current ChainState is not pointing to the specified StateAndRef`() {

        // Arrange
        val oldChainState = createStateAndRef(Customer(NULL_PARTY, "John", "Smith", Instant.MIN))
        val newChainState = oldChainState.state.data.copy(previousStateRef = EMPTY_STATE_REF)

        // Act
        val result = newChainState.isPointingTo(oldChainState)

        // Assert
        assertFalse(result)
    }

    private fun <T : ChainState> createStateAndRef(state: T): StateAndRef<T> = StateAndRef(
        state = TransactionState(state, state.requiredContractClassName ?: "", NOTARY.party),
        ref = RANDOM_STATE_REF
    )
}
