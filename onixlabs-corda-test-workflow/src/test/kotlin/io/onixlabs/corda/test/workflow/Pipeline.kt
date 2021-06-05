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

package io.onixlabs.corda.test.workflow

import net.corda.core.flows.FlowLogic
import net.corda.core.utilities.getOrThrow
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import java.time.Duration

class Pipeline<T>(val result: T, val network: MockNetwork, val timeout: Duration) {

    companion object {
        fun create(network: MockNetwork, duration: Duration = Duration.ofSeconds(30)): Pipeline<Any?> {
            return Pipeline(null, network, duration)
        }
    }

    fun <U> run(node: StartedMockNode, action: (T) -> FlowLogic<U>): Pipeline<U> {
        val future = node.startFlow(action(result))
        network.runNetwork()
        return Pipeline(future.getOrThrow(timeout), network, timeout)
    }

    fun finally(action: (T) -> Unit) {
        action(result)
    }
}
