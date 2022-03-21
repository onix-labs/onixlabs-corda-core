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

package io.onixlabs.corda.core.contract

import io.onixlabs.corda.core.contract.StatePosition.*
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.LedgerTransaction

/**
 * Determines the position of a state within a transaction.
 *
 * @property INPUT The state is an input state in the transaction.
 * @property OUTPUT The state is an output state in the transaction.
 * @property REFERENCE The state is a reference state in the transaction.
 */
@CordaSerializable
enum class StatePosition {
    INPUT,
    OUTPUT,
    REFERENCE;

    /**
     * Obtains a [List] of [StateAndRef] of type [T] from the specified [LedgerTransaction].
     *
     * @param T The underlying [ContractState] type to obtain from the transaction.
     * @param transaction The [LedgerTransaction] from which to obtain [StateAndRef] instances of type [T].
     * @param type The [ContractState] type to obtain from the transaction.
     * @return Returns a [List] of [StateAndRef] of type [T] from the specified [LedgerTransaction].
     */
    fun <T : ContractState> getStateAndRefs(transaction: LedgerTransaction, type: Class<T>): List<StateAndRef<T>> = when (this) {
        INPUT -> transaction.inRefsOfType(type)
        OUTPUT -> transaction.outRefsOfType(type)
        REFERENCE -> transaction.referenceInputRefsOfType(type)
    }

    /**
     * Obtains a [List] of [StateAndRef] of type [T] from the specified [LedgerTransaction].
     *
     * @param T The underlying [ContractState] type to obtain from the transaction.
     * @param transaction The [LedgerTransaction] from which to obtain instances of type [T].
     * @return Returns a [List] of [StateAndRef] of type [T] from the specified [LedgerTransaction].
     */
    inline fun <reified T : ContractState> getStateAndRefs(transaction: LedgerTransaction): List<StateAndRef<T>> {
        return getStateAndRefs(transaction, T::class.java)
    }

    /**
     * Obtains a [List] of type [T] from the specified [LedgerTransaction].
     *
     * @param T The underlying [ContractState] type to obtain from the transaction.
     * @param transaction The [LedgerTransaction] from which to obtain instances of type [T].
     * @param type The [ContractState] type to obtain from the transaction.
     * @return Returns a [List] of type [T] from the specified [LedgerTransaction].
     */
    fun <T : ContractState> getStates(transaction: LedgerTransaction, type: Class<T>): List<T> {
        return getStateAndRefs(transaction, type).map { it.state.data }
    }

    /**
     * Obtains a [List] of type [T] from the specified [LedgerTransaction].
     *
     * @param T The underlying [ContractState] type to obtain from the transaction.
     * @param transaction The [LedgerTransaction] from which to obtain instances of type [T].
     * @return Returns a [List] of type [T] from the specified [LedgerTransaction].
     */
    inline fun <reified T : ContractState> getStates(transaction: LedgerTransaction): List<T> {
        return getStates(transaction, T::class.java)
    }
}
