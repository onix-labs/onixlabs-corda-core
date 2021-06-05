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

package io.onixlabs.corda.core.contract

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.node.ServiceHub
import net.corda.core.transactions.LedgerTransaction

private const val MESSAGE = "The expected state may have not been witnessed, or may have been exited by this node."

/**
 * Resolves a [ContractState] using a [CordaRPCOps] instance.
 *
 * @param cordaRPCOps The [CordaRPCOps] instance to use to resolve the state.
 * @param message The exception message to throw if the state cannot be resolved.
 * @return Returns the resolved [ContractState], or throws an exception if the state cannot be resolved.
 */
fun <T : ContractState> SingularResolvable<T>.resolveOrThrow(
    cordaRPCOps: CordaRPCOps,
    message: () -> String = { MESSAGE }
): StateAndRef<T> = resolve(cordaRPCOps) ?: throw IllegalArgumentException(message())

/**
 * Resolves a [ContractState] using a [ServiceHub] instance.
 *
 * @param serviceHub The [ServiceHub] instance to use to resolve the state.
 * @param message The exception message to throw if the state cannot be resolved.
 * @return Returns the resolved [ContractState], or throws an exception if the state cannot be resolved.
 */
fun <T : ContractState> SingularResolvable<T>.resolveOrThrow(
    serviceHub: ServiceHub,
    message: () -> String = { MESSAGE }
): StateAndRef<T> = resolve(serviceHub) ?: throw IllegalArgumentException(message())

/**
 * Resolves a [ContractState] using a [LedgerTransaction] instance.
 *
 * @param transaction The [LedgerTransaction] instance to use to resolve the state.
 * @param resolution The transaction resolution method to use to resolve the [ContractState] instance.
 * @param message The exception message to throw if the state cannot be resolved.
 * @return Returns the resolved [ContractState], or throws an exception if the state cannot be resolved.
 */
fun <T : ContractState> SingularResolvable<T>.resolveOrThrow(
    transaction: LedgerTransaction,
    resolution: TransactionResolution,
    message: () -> String = { MESSAGE }
): StateAndRef<T> = resolve(transaction, resolution) ?: throw IllegalArgumentException(message())
