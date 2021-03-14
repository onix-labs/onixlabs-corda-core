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

import io.onixlabs.corda.core.contract.TransactionResolution.*
import net.corda.core.serialization.CordaSerializable

/**
 * Defines how to resolve states from a transaction.
 *
 * @property INPUT Resolve a state from the transaction input states.
 * @property OUTPUT Resolve a state from the transaction output states.
 * @property REFERENCE Resolve a state from the transaction reference states.
 */
@CordaSerializable
enum class TransactionResolution { INPUT, OUTPUT, REFERENCE }
