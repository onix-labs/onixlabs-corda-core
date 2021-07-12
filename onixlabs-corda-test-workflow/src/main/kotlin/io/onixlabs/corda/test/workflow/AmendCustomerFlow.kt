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
import io.onixlabs.corda.core.workflow.*
import io.onixlabs.corda.test.contract.Customer
import io.onixlabs.corda.test.contract.CustomerContract
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker

class AmendCustomerFlow(
    private val oldCustomer: StateAndRef<Customer>,
    private val newCustomer: Customer,
    private val notary: Party,
    private val sessions: Set<FlowSession> = emptySet(),
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<SignedTransaction>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(
            BuildTransactionStep,
            VerifyTransactionStep,
            SignTransactionStep,
            SendStatesToRecordStep,
            FinalizeTransactionStep
        )
    }

    @Suspendable
    override fun call(): SignedTransaction {
        val transaction = buildTransaction(notary) {
            addInputState(oldCustomer)
            addOutputState(newCustomer)
            addCommand(CustomerContract.Amend, newCustomer.owner.owningKey)
        }

        verifyTransaction(transaction)
        val signedTransaction = signTransaction(transaction)
        return finalizeTransaction(signedTransaction, sessions)
    }

    @StartableByRPC
    @StartableByService
    @InitiatingFlow(version = 1)
    class Initiator(
        private val oldCustomer: StateAndRef<Customer>,
        private val newCustomer: Customer,
        private val notary: Party? = null,
        private val observers: Set<Party> = emptySet()
    ) : FlowLogic<SignedTransaction>() {

        private companion object {
            object AmendingCustomerStep : ProgressTracker.Step("Amended customer.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(AmendingCustomerStep)

        @Suspendable
        override fun call(): SignedTransaction {
            currentStep(AmendingCustomerStep)
            return subFlow(
                AmendCustomerFlow(
                    oldCustomer,
                    newCustomer,
                    notary ?: getPreferredNotary { firstNotary },
                    initiateFlows(observers, oldCustomer.state.data, newCustomer),
                    AmendingCustomerStep.childProgressTracker()
                )
            )
        }
    }
}
