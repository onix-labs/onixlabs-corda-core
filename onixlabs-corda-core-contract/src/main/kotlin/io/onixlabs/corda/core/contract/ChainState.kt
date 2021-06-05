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
import net.corda.core.contracts.StateRef

/**
 * Defines a Corda chain state.
 * A chain state references the previous state in the sequence, or null if it's the first state in a chain.
 *
 * @property previousStateRef The reference to the previous state in the sequence, or null if it's the first state in a chain.
 */
interface ChainState : ContractState {
    val previousStateRef: StateRef?
}
