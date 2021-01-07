/**
 * Copyright 2020 Matthew Layton
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

package io.onixlabs.corda.core.workflow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort
import kotlin.reflect.jvm.jvmErasure

/**
 * Represents the base class for implementing flows to find multiple states.
 *
 * @property criteria The vault query criteria expression.
 * @property pageSpecification The vault query page specification.
 * @property contractStateType The vault query contract state type.
 */
@StartableByRPC
@StartableByService
abstract class FindStatesFlow<T : ContractState> : FlowLogic<List<StateAndRef<T>>>() {
    protected abstract val criteria: QueryCriteria
    protected abstract val pageSpecification: PageSpecification
    protected abstract val sorting: Sort

    @Suppress("UNCHECKED_CAST")
    protected val contractStateType: Class<T>
        get() = javaClass.kotlin.supertypes[0].arguments[0].type?.jvmErasure?.javaObjectType as Class<T>

    @Suspendable
    override fun call(): List<StateAndRef<T>> {
        return serviceHub.vaultService.queryBy(contractStateType, criteria, pageSpecification, sorting).states
    }
}
