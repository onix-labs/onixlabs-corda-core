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

import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.identity.CordaX500Name
import net.corda.testing.core.DUMMY_NOTARY_NAME
import net.corda.testing.core.TestIdentity
import java.time.Instant

val IDENTITY_A = TestIdentity(CordaX500Name("PartyA", "London", "GB"))
val IDENTITY_B = TestIdentity(CordaX500Name("PartyB", "New York", "US"))
val IDENTITY_C = TestIdentity(CordaX500Name("PartyC", "Paris", "FR"))
val NOTARY = TestIdentity(DUMMY_NOTARY_NAME)

val CUSTOMER_A = Customer(IDENTITY_A.party, "John", "Smith", Instant.MIN)
val CUSTOMER_B = Customer(IDENTITY_B.party, "Joan", "Smith", Instant.MAX)

val REWARD_A = Reward(IDENTITY_C.party, IDENTITY_A.party, 100, CUSTOMER_A.linearId)
val REWARD_B = Reward(IDENTITY_C.party, IDENTITY_B.party, 100, CUSTOMER_B.linearId)

val EMPTY_STATE_REF: StateRef get() = StateRef(SecureHash.zeroHash, 0)
val RANDOM_STATE_REF: StateRef get() = StateRef(SecureHash.randomSHA256(), 0)