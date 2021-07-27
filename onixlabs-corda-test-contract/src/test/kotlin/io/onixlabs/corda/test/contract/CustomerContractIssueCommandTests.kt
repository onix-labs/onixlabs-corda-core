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

package io.onixlabs.corda.test.contract

import io.onixlabs.corda.test.*
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test

class CustomerContractIssueCommandTests : ContractTest() {

    @Test
    fun `On customer issuing, the transaction must include the Issue command`() {
        services.ledger {
            transaction {
                output(CustomerContract.ID, CUSTOMER_A)
                fails()
                command(keysOf(IDENTITY_A), CustomerContract.Issue)
                verifies()
            }
        }
    }

    @Test
    fun `On customer issuing, zero customer states must be consumed`() {
        services.ledger {
            transaction {
                input(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_A), CustomerContract.Issue)
                failsWith(CustomerContract.Issue.CONTRACT_RULE_INPUTS)
            }
        }
    }

    @Test
    fun `On customer issuing, only one customer state must be created`() {
        services.ledger {
            transaction {
                output(CustomerContract.ID, CUSTOMER_A)
                output(CustomerContract.ID, CUSTOMER_B)
                command(keysOf(IDENTITY_A), CustomerContract.Issue)
                failsWith(CustomerContract.Issue.CONTRACT_RULE_OUTPUTS)
            }
        }
    }

    @Test
    fun `On customer issuing, the created customer's first name must not be blank`() {
        services.ledger {
            transaction {
                output(CustomerContract.ID, CUSTOMER_A.copy(firstName = ""))
                command(keysOf(IDENTITY_A), CustomerContract.Issue)
                failsWith(CustomerContract.Issue.CONTRACT_RULE_FIRST_NAME)
            }
        }
    }

    @Test
    fun `On customer issuing, the created customer's last name must not be blank`() {
        services.ledger {
            transaction {
                output(CustomerContract.ID, CUSTOMER_A.copy(lastName = ""))
                command(keysOf(IDENTITY_A), CustomerContract.Issue)
                failsWith(CustomerContract.Issue.CONTRACT_RULE_LAST_NAME)
            }
        }
    }

    @Test
    fun `On customer issuing, the created customer's previous state reference must be null`() {
        services.ledger {
            transaction {
                output(CustomerContract.ID, CUSTOMER_A.copy(previousStateRef = EMPTY_STATE_REF))
                command(keysOf(IDENTITY_A), CustomerContract.Issue)
                failsWith(CustomerContract.Issue.CONTRACT_RULE_PREVIOUS_STATE_REF)
            }
        }
    }

    @Test
    fun `On customer issuing, the created customer's owner must sign the transaction`() {
        services.ledger {
            transaction {
                output(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_B), CustomerContract.Issue)
                failsWith(CustomerContract.Issue.CONTRACT_RULE_SIGNERS)
            }
        }
    }
}
