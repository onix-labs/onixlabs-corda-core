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

import io.onixlabs.corda.core.getArgumentType
import io.onixlabs.corda.core.toTypedClass
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.LedgerTransaction

/**
 * Represents the base class for implementing plural (one-to-many) contract state resolvers.
 *
 * @param T The underlying [ContractState] type.
 * @property criteria The vault query criteria which will be used to resolve contract states.
 * @property contractStateType The contract state class to resolve.
 */
abstract class AbstractPluralResolvable<T> : PluralResolvable<T> where T : ContractState {

    protected abstract val criteria: QueryCriteria
    protected open val contractStateType: Class<T> get() = javaClass.getArgumentType(0).toTypedClass()

    /**
     * Resolves a [ContractState] using a [CordaRPCOps] instance.
     *
     * @param cordaRPCOps The [CordaRPCOps] instance to use to resolve the state.
     * @return Returns a list of resolved [ContractState] elements, or an empty list if no matching state is found.
     */
    override fun resolve(cordaRPCOps: CordaRPCOps): List<StateAndRef<T>> {
        return cordaRPCOps.vaultQueryByCriteria(criteria, contractStateType).states
    }

    /**
     * Resolves a [ContractState] using a [ServiceHub] instance.
     *
     * @param serviceHub The [ServiceHub] instance to use to resolve the state.
     * @return Returns a list of resolved [ContractState] elements, or an empty list if no matching state is found.
     */
    override fun resolve(serviceHub: ServiceHub): List<StateAndRef<T>> {
        return serviceHub.vaultService.queryBy(contractStateType, criteria).states
    }

    /**
     * Resolves a [ContractState] using a [LedgerTransaction] instance.
     *
     * @param transaction The [LedgerTransaction] instance to use to resolve the state.
     * @param position The position of the [ContractState]  instances to resolve in the transaction.
     * @return Returns a list of resolved [ContractState] elements, or an empty list if no matching state is found.
     */
    override fun resolve(transaction: LedgerTransaction, position: StatePosition): List<StateAndRef<T>> {
        return position.getStateAndRefs(transaction, contractStateType).filter { isPointingTo(it) }
    }
}
