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

package io.onixlabs.corda.test.workflow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.unwrap

class SpendRewardFlowHandler(private val session: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        return subFlow(ReceiveFinalityFlow(session, statesToRecord = StatesToRecord.ALL_VISIBLE))
    }

    @InitiatedBy(SpendRewardFlow.Initiator::class)
    private class Handler(private val session: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            val isRequiredToSign = session.receive<Boolean>().unwrap { it }

            if (isRequiredToSign) {
                subFlow(object : SignTransactionFlow(session) {
                    override fun checkTransaction(stx: SignedTransaction) {
                    }
                })
            }

            return subFlow(IssueRewardFlowHandler(session))
        }
    }
}