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
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort

/**
 * Represents a vault service adapter implementation for [CordaRPCOps].
 *
 * @param cordaRPCOps The [CordaRPCOps] instance which will be used to query or track the vault.
 * @param contractStateType The type of contract state used by the vault service adapter.
 */
internal class VaultAdapterCordaRPCOps<T : ContractState>(
    private val cordaRPCOps: CordaRPCOps,
    contractStateType: Class<T>
) : VaultAdapter<T>(contractStateType) {

    /**
     * Queries the vault.
     *
     * @param criteria The query criteria to use in the vault query.
     * @param paging The paging specification to use in the vault query.
     * @param sorting The sorting to use in the vault query.
     * @return Returns a vault page of contract state items.
     */
    override fun queryBy(
        criteria: QueryCriteria,
        paging: PageSpecification,
        sorting: Sort
    ): Vault.Page<T> {
        return cordaRPCOps.vaultQueryBy(criteria, paging, sorting, contractStateType)
    }

    /**
     * Tracks the vault.
     *
     * @param criteria The query criteria to use in vault tracking.
     * @param paging The paging specification to use in vault tracking.
     * @param sorting The sorting to use in vault tracking.
     * @return Returns a data feed of tracked items from the vault.
     */
    override fun trackBy(
        criteria: QueryCriteria,
        paging: PageSpecification,
        sorting: Sort
    ): DataFeed<Vault.Page<T>, Vault.Update<T>> {
        return cordaRPCOps.vaultTrackBy(criteria, paging, sorting, contractStateType)
    }
}
