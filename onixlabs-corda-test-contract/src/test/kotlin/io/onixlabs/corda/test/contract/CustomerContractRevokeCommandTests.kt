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

package io.onixlabs.corda.test.contract

import io.onixlabs.corda.test.*
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test

class CustomerContractRevokeCommandTests : ContractTest() {

    @Test
    fun `On customer revoking, the transaction must include the Revoke command`() {
        services.ledger {
            transaction {
                input(CustomerContract.ID, CUSTOMER_A)
                fails()
                command(keysOf(IDENTITY_A), CustomerContract.Revoke)
                verifies()
            }
        }
    }

    @Test
    fun `On customer revoking, only one customer state must be consumed`() {
        services.ledger {
            transaction {
                input(CustomerContract.ID, CUSTOMER_A)
                input(CustomerContract.ID, CUSTOMER_B)
                command(keysOf(IDENTITY_A), CustomerContract.Revoke)
                failsWith(CustomerContract.Revoke.CONTRACT_RULE_INPUTS)
            }
        }
    }

    @Test
    fun `On customer revoking, zero customer states must be created`() {
        services.ledger {
            transaction {
                input(CustomerContract.ID, CUSTOMER_A)
                output(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_A), CustomerContract.Revoke)
                failsWith(CustomerContract.Revoke.CONTRACT_RULE_OUTPUTS)
            }
        }
    }

    @Test
    fun `On customer revoking, the consumed customer's owner must sign the transaction`() {
        services.ledger {
            transaction {
                input(CustomerContract.ID, CUSTOMER_A)
                command(keysOf(IDENTITY_B), CustomerContract.Revoke)
                failsWith(CustomerContract.Revoke.CONTRACT_RULE_SIGNERS)
            }
        }
    }
}
