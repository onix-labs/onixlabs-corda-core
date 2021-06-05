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

import co.paralleluniverse.fibers.Suspendable
import io.onixlabs.corda.test.contract.Customer
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService

@StartableByRPC
@StartableByService
class GetCustomerRewardBalanceFlow(private val customer: Customer) : FlowLogic<Int>() {

    @Suspendable
    override fun call(): Int {
        return customer.rewards.resolve(serviceHub).sumBy { it.state.data.points }
    }
}
