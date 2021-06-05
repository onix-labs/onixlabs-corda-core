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

package io.onixlabs.corda.test.workflow.customer

import io.onixlabs.corda.test.contract.Customer
import io.onixlabs.corda.test.workflow.AmendCustomerFlow
import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.IssueCustomerFlow
import io.onixlabs.corda.test.workflow.Pipeline
import net.corda.core.transactions.SignedTransaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.test.assertEquals

class AmendCustomerFlowTests : FlowTest() {

    private lateinit var customer: Customer
    private lateinit var transaction: SignedTransaction

    override fun initialize() {
        Pipeline
            .create(network)
            .run(nodeA) {
                IssueCustomerFlow.Initiator(CUSTOMER_1, observers = setOf(partyB, partyC))
            }
            .run(nodeA) {
                val issuedCustomer = it.tx.outRefsOfType<Customer>().single()
                val amendedCustomer = issuedCustomer.state.data.copy(previousStateRef = issuedCustomer.ref)
                AmendCustomerFlow.Initiator(issuedCustomer, amendedCustomer, observers = setOf(partyB, partyC))
            }
            .finally {
                transaction = it
                customer = it.tx.outputsOfType<Customer>().single()
            }
    }

    @Test
    fun `IssueCustomerFlow transaction should be signed by the initiator`() {
        transaction.verifyRequiredSignatures()
    }

    @Test
    fun `IssueCustomerFlow should record a transaction for the owner and observers`() {
        listOf(nodeA, nodeB, nodeC).forEach {
            it.transaction {
                val recordedTransaction = it.services.validatedTransactions.getTransaction(transaction.id)
                    ?: fail("Failed to find a recorded transaction with id: ${transaction.id}.")

                assertEquals(transaction, recordedTransaction)
            }
        }
    }

    @Test
    fun `IssueCustomerFlow should record a customer for the owner and observers`() {
        listOf(nodeA, nodeB, nodeC).forEach {
            it.transaction {
                val recordedTransaction = it.services.validatedTransactions.getTransaction(transaction.id)
                    ?: fail("Failed to find a recorded transaction with id: ${transaction.id}.")

                val recordedCustomer = recordedTransaction
                    .tx.outputsOfType<Customer>().singleOrNull()
                    ?: fail("Failed to find a recorded customer.")

                assertEquals(customer, recordedCustomer)
            }
        }
    }
}
