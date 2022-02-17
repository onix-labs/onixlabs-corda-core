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

package io.onixlabs.corda.core.services

import net.corda.core.contracts.ContractState
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.DataFeed
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria
import net.corda.core.node.services.vault.Sort

/**
 * Represents a service for managing vault querying and tracking.
 *
 * @param T The underlying [ContractState] type.
 * @param adapter The [VaultAdapter] which will be used for performing vault queries and tracking.
 * @param contractStateType The [Class] of the contract state type to bind this vault service to.
 */
class VaultService<T : ContractState> private constructor(
    private val adapter: VaultAdapter<T>,
    internal val contractStateType: Class<T>
) {

    companion object {

        /**
         * Creates a vault service from a [CordaRPCOps] instance.
         *
         * @param cordaRPCOps The [CordaRPCOps] instance from which to create a [VaultService] instance.
         * @param contractStateType The [Class] of the [ContractState] type to bind the vault service to.
         */
        fun <T : ContractState> create(cordaRPCOps: CordaRPCOps, contractStateType: Class<T>): VaultService<T> {
            return VaultService(VaultAdapterCordaRPCOps(cordaRPCOps, contractStateType), contractStateType)
        }

        /**
         * Creates a vault service from a [CordaRPCOps] instance.
         *
         * @param T The underlying [ContractState] type to bind the vault service to.
         * @param cordaRPCOps The [CordaRPCOps] instance from which to create a [VaultService] instance.
         */
        inline fun <reified T : ContractState> create(cordaRPCOps: CordaRPCOps): VaultService<T> {
            return create(cordaRPCOps, T::class.java)
        }

        /**
         * Creates a vault service from a [ServiceHub] instance.
         *
         * @param serviceHub The [ServiceHub] instance from which to create a [VaultService] instance.
         * @param contractStateType The [Class] of the [ContractState] type to bind the vault service to.
         */
        fun <T : ContractState> create(serviceHub: ServiceHub, contractStateType: Class<T>): VaultService<T> {
            return VaultService(VaultAdapterServiceHub(serviceHub, contractStateType), contractStateType)
        }

        /**
         * Creates a vault service from a [ServiceHub] instance.
         *
         * @param T The underlying [ContractState] type to bind the vault service to.
         * @param serviceHub The [ServiceHub] instance from which to create a [VaultService] instance.
         */
        inline fun <reified T : ContractState> create(serviceHub: ServiceHub): VaultService<T> {
            return create(serviceHub, T::class.java)
        }
    }

    /**
     * The default criteria for tracking the vault.
     */
    internal val defaultTrackingCriteria = VaultQueryCriteria(
        contractStateTypes = setOf(contractStateType),
        status = Vault.StateStatus.ALL
    )

    /**
     * The default criteria for querying the vault.
     */
    internal val defaultQueryCriteria = VaultQueryCriteria(
        contractStateTypes = setOf(contractStateType),
        status = Vault.StateStatus.UNCONSUMED
    )

    /**
     * Performs a vault query given the specified criteria, paging and sorting.
     *
     * @param criteria The query criteria to use in the vault query.
     * @param paging The paging specification to use in the vault query.
     * @param sorting The sorting to use in the vault query.
     * @return Returns a vault page of contract state items.
     */
    fun queryBy(
        criteria: QueryCriteria = defaultQueryCriteria,
        paging: PageSpecification = DEFAULT_PAGE_SPECIFICATION,
        sorting: Sort = DEFAULT_SORTING
    ): Vault.Page<T> {
        return adapter.queryBy(criteria, paging, sorting)
    }

    /**
     * Tracks changes in the vault given the specified criteria, paging and sorting.
     *
     * @param criteria The query criteria to use in vault tracking.
     * @param paging The paging specification to use in vault tracking.
     * @param sorting The sorting to use in vault tracking.
     * @return Returns a data feed of tracked items from the vault.
     */
    fun trackBy(
        criteria: QueryCriteria = defaultTrackingCriteria,
        paging: PageSpecification = DEFAULT_PAGE_SPECIFICATION,
        sorting: Sort = DEFAULT_SORTING
    ): DataFeed<Vault.Page<T>, Vault.Update<T>> {
        return adapter.trackBy(criteria, paging, sorting)
    }
}
