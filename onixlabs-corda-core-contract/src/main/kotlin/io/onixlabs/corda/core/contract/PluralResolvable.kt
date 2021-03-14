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

package io.onixlabs.corda.core.contract

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.node.ServiceHub
import net.corda.core.transactions.LedgerTransaction

/**
 * Defines an object which resolves a collection of [ContractState].
 *
 * @param T The underlying [ContractState] type to resolve.
 */
interface PluralResolvable<T> where T : ContractState {

    /**
     * Resolves a [ContractState] using a [CordaRPCOps] instance.
     *
     * @param cordaRPCOps The [CordaRPCOps] instance to use to resolve the state.
     * @return Returns a list of resolved [ContractState] elements, or an empty list if no matching state is found.
     */
    fun resolve(cordaRPCOps: CordaRPCOps): List<StateAndRef<T>>

    /**
     * Resolves a [ContractState] using a [ServiceHub] instance.
     *
     * @param serviceHub The [ServiceHub] instance to use to resolve the state.
     * @return Returns a list of resolved [ContractState] elements, or an empty list if no matching state is found.
     */
    fun resolve(serviceHub: ServiceHub): List<StateAndRef<T>>

    /**
     * Resolves a [ContractState] using a [LedgerTransaction] instance.
     *
     * @param transaction The [LedgerTransaction] instance to use to resolve the state.
     * @param resolution The transaction resolution method to use to resolve the [ContractState] instance.
     * @return Returns a list of resolved [ContractState] elements, or an empty list if no matching state is found.
     */
    fun resolve(transaction: LedgerTransaction, resolution: TransactionResolution): List<StateAndRef<T>>

    /**
     * Determines whether this [PluralResolvable] is pointing to the specified [StateAndRef] instance.
     *
     * @param stateAndRef The [StateAndRef] to determine being pointed to.
     * @return Returns true if this [PluralResolvable] is pointing to the specified [StateAndRef]; otherwise, false.
     */
    fun isPointingTo(stateAndRef: StateAndRef<T>): Boolean
}
