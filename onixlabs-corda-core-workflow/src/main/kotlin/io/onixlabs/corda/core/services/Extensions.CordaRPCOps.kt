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

package io.onixlabs.corda.core.services

import net.corda.core.contracts.ContractState
import net.corda.core.messaging.CordaRPCOps

/**
 * Creates a vault service, bound to the specified [ContractState] type.
 *
 * @param T The underlying [ContractState] type to bind the vault service to.
 * @param contractStateType The [Class] of the [ContractState] type to bind the vault service to.
 * @return Returns a [VaultService] instance bound to the underlying [ContractState] type.
 */
fun <T : ContractState> CordaRPCOps.vaultServiceFor(contractStateType: Class<T>): VaultService<T> {
    return VaultService.create(this, contractStateType)
}

/**
 * Creates a vault service, bound to the specified [ContractState] type.
 *
 * @param T The underlying [ContractState] type to bind the vault service to.
 * @return Returns a [VaultService] instance bound to the underlying [ContractState] type.
 */
inline fun <reified T : ContractState> CordaRPCOps.vaultServiceFor(): VaultService<T> {
    return vaultServiceFor(T::class.java)
}
