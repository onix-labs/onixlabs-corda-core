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

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.TransactionState

/**
 * Casts a [StateAndRef] of an unknown [ContractState] to a [StateAndRef] of type [T].
 *
 * @param T The underlying [ContractState] type to cast to.
 * @param contractStateClass The [ContractState] class to cast to.
 * @return Returns a [StateAndRef] of type [T].
 * @throws ClassCastException if the unknown [ContractState] type cannot be cast to [T].
 */
fun <T> StateAndRef<*>.cast(contractStateClass: Class<T>): StateAndRef<T> where T : ContractState = with(state) {
    StateAndRef(TransactionState(contractStateClass.cast(data), contract, notary, encumbrance, constraint), ref)
}

/**
 * Casts a [StateAndRef] of an unknown [ContractState] to a [StateAndRef] of type [T].
 *
 * @param T The underlying [ContractState] type to cast to.
 * @return Returns a [StateAndRef] of type [T].
 * @throws ClassCastException if the unknown [ContractState] type cannot be cast to [T].
 */
inline fun <reified T> StateAndRef<*>.cast(): StateAndRef<T> where T : ContractState {
    return cast(T::class.java)
}

/**
 * Casts an iterable of [StateAndRef] of an unknown [ContractState] to a list of [StateAndRef] of type [T].
 *
 * @param T The underlying [ContractState] type to cast to.
 * @param contractStateClass The [ContractState] class to cast to.
 * @return Returns a list of [StateAndRef] of type [T].
 * @throws ClassCastException if the unknown [ContractState] type cannot be cast to [T].
 */
fun <T> Iterable<StateAndRef<*>>.cast(contractStateClass: Class<T>): List<StateAndRef<T>> where T : ContractState {
    return map { it.cast(contractStateClass) }
}

/**
 * Casts an iterable of [StateAndRef] of an unknown [ContractState] to a list of [StateAndRef] of type [T].
 *
 * @param T The underlying [ContractState] type to cast to.
 * @return Returns a list of [StateAndRef] of type [T].
 * @throws ClassCastException if the unknown [ContractState] type cannot be cast to [T].
 */
inline fun <reified T> Iterable<StateAndRef<*>>.cast(): List<StateAndRef<T>> where T : ContractState {
    return cast(T::class.java)
}
