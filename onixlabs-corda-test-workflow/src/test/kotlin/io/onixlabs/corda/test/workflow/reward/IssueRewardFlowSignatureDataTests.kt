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

package io.onixlabs.corda.test.workflow.reward

import io.onixlabs.corda.test.workflow.FlowTest
import io.onixlabs.corda.test.workflow.IssueCustomerFlow
import io.onixlabs.corda.test.workflow.IssueRewardFlow
import io.onixlabs.corda.test.workflow.Pipeline
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.SignatureException
import kotlin.test.assertEquals

class IssueRewardFlowSignatureDataTests : FlowTest() {

    private val expectedMessage =
        "Failed to obtain a private key matching the specified public key from the key management service."

    @Test
    fun `IssueRewardFlow cannot issue a reward when the owner is the initiator`() {
        val exception = assertThrows<SignatureException> {
            Pipeline
                .create(network)
                .run(nodeA) {
                    IssueCustomerFlow.Initiator(CUSTOMER_1)
                }
                .run(nodeA) {
                    IssueRewardFlow.Initiator(REWARD_A1)
                }
        }

        assertEquals(expectedMessage, exception.message)
    }
}
