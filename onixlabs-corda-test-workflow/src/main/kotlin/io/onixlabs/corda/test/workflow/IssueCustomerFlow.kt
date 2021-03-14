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
import io.onixlabs.corda.core.workflow.firstNotary
import io.onixlabs.corda.core.workflow.getPreferredNotary
import io.onixlabs.corda.core.workflow.initiateFlows
import io.onixlabs.corda.test.contract.Customer
import io.onixlabs.corda.test.contract.CustomerContract
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

class IssueCustomerFlow(
    private val customer: Customer,
    private val notary: Party,
    private val sessions: Set<FlowSession> = emptySet()
) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val transaction = with(TransactionBuilder(notary)) {
            addOutputState(customer)
            addCommand(CustomerContract.Issue, customer.owner.owningKey)
        }

        transaction.verify(serviceHub)
        val signedTransaction = serviceHub.signInitialTransaction(transaction, customer.owner.owningKey)
        return subFlow(FinalityFlow(signedTransaction, sessions))
    }

    @StartableByRPC
    @StartableByService
    @InitiatingFlow(version = 1)
    class Initiator(
        private val customer: Customer,
        private val notary: Party? = null,
        private val observers: Set<Party> = emptySet()
    ) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            val sessions = initiateFlows(observers)
            return subFlow(
                IssueCustomerFlow(
                    customer,
                    notary ?: getPreferredNotary { firstNotary },
                    sessions
                )
            )
        }
    }
}
