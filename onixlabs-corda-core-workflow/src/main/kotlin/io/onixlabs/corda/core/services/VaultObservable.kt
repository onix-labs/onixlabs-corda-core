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
import net.corda.core.contracts.StateRef
import net.corda.core.contracts.TransactionState
import net.corda.core.node.services.Vault
import net.corda.core.serialization.CordaSerializable
import java.util.*

/**
 * Represents an observable vault state.
 *
 * @param T The underlying [ContractState] type.
 * @property state The [TransactionState] of the observable.
 * @property ref The [StateRef] of the observable.
 * @property status The [Vault.StateStatus] of the observable.
 * @property flowId The ID of the flow that was responsible for consuming or creating the state.
 */
@CordaSerializable
class VaultObservable<T : ContractState>(
    val state: TransactionState<T>,
    val ref: StateRef,
    val status: Vault.StateStatus,
    val flowId: UUID?
)
