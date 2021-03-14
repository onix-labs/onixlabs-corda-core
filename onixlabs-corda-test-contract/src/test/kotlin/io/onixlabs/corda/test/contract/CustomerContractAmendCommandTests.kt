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

package io.onixlabs.corda.test.contract

import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test

class CustomerContractAmendCommandTests : ContractTest() {

    @Test
    fun `On customer amending, the transaction must include the Amend command`() {
        services.ledger {
            transaction {
                val issuedCustomer = issue(CUSTOMER_A)
                input(issuedCustomer.ref)
                output(CustomerContract.ID, issuedCustomer.evolve())
                fails()
                command(keysOf(IDENTITY_A), CustomerContract.Amend)
                verifies()
            }
        }
    }

    @Test
    fun `On customer amending, only one customer state must be consumed`() {
        services.ledger {
            transaction {
                val issuedCustomer = issue(CUSTOMER_A)
                output(CustomerContract.ID, issuedCustomer.evolve())
                command(keysOf(IDENTITY_A), CustomerContract.Amend)
                failsWith(CustomerContract.Amend.CONTRACT_RULE_INPUTS)
            }
        }
    }

    @Test
    fun `On customer amending, only one customer state must be created`() {
        services.ledger {
            transaction {
                val issuedCustomer = issue(CUSTOMER_A)
                input(issuedCustomer.ref)
                output(CustomerContract.ID, issuedCustomer.evolve())
                output(CustomerContract.ID, issuedCustomer.evolve())
                command(keysOf(IDENTITY_A), CustomerContract.Amend)
                failsWith(CustomerContract.Amend.CONTRACT_RULE_OUTPUTS)
            }
        }
    }

    @Test
    fun `On customer amending, the created customer's first name must not be blank`() {
        services.ledger {
            transaction {
                val issuedCustomer = issue(CUSTOMER_A)
                input(issuedCustomer.ref)
                output(CustomerContract.ID, issuedCustomer.evolve().copy(firstName = ""))
                command(keysOf(IDENTITY_A), CustomerContract.Amend)
                failsWith(CustomerContract.Amend.CONTRACT_RULE_FIRST_NAME)
            }
        }
    }

    @Test
    fun `On customer amending, the created customer's last name must not be blank`() {
        services.ledger {
            transaction {
                val issuedCustomer = issue(CUSTOMER_A)
                input(issuedCustomer.ref)
                output(CustomerContract.ID, issuedCustomer.evolve().copy(lastName = ""))
                command(keysOf(IDENTITY_A), CustomerContract.Amend)
                failsWith(CustomerContract.Amend.CONTRACT_RULE_LAST_NAME)
            }
        }
    }

    @Test
    fun `On customer amending, the created customer's previous state reference must be equal to the consumed customer's state reference`() {
        services.ledger {
            transaction {
                val issuedCustomer = issue(CUSTOMER_A)
                input(issuedCustomer.ref)
                output(CustomerContract.ID, issuedCustomer.evolve().copy(previousStateRef = EMPTY_STATE_REF))
                command(keysOf(IDENTITY_A), CustomerContract.Amend)
                failsWith(CustomerContract.Amend.CONTRACT_RULE_PREVIOUS_STATE_REF)
            }
        }
    }

    @Test
    fun `On customer amending, the created customer's owner must sign the transaction`() {
        services.ledger {
            transaction {
                val issuedCustomer = issue(CUSTOMER_A)
                input(issuedCustomer.ref)
                output(CustomerContract.ID, issuedCustomer.evolve())
                command(keysOf(IDENTITY_B), CustomerContract.Amend)
                failsWith(CustomerContract.Amend.CONTRACT_RULE_SIGNERS)
            }
        }
    }
}
