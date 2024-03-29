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

import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef

/**
 * Determines whether the current state is pointing to the specified [StateAndRef].
 *
 * @param stateRef The [StateRef] of the state being pointed to.
 * @return Returns true if the current state is pointing to the specified [StateRef]; otherwise, false.
 */
fun <T> T.isPointingTo(stateRef: StateRef): Boolean where T : ChainState {
    return stateRef == previousStateRef
}

/**
 * Determines whether the current state is pointing to the specified [StateAndRef].
 *
 * @param stateAndRef The [StateAndRef] of the state being pointed to.
 * @return Returns true if the current state is pointing to the specified [StateAndRef]; otherwise, false.
 */
fun <T> T.isPointingTo(stateAndRef: StateAndRef<T>): Boolean where T : ChainState {
    return isPointingTo(stateAndRef.ref)
}
