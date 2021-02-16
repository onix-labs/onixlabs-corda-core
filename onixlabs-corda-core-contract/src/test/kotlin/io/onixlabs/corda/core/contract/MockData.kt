package io.onixlabs.corda.core.contract

import net.corda.core.identity.CordaX500Name
import net.corda.testing.core.DUMMY_NOTARY_NAME
import net.corda.testing.core.TestIdentity

val IDENTITY_A = TestIdentity(CordaX500Name("PartyA", "London", "GB"))
val IDENTITY_B = TestIdentity(CordaX500Name("PartyB", "New York", "US"))
val IDENTITY_C = TestIdentity(CordaX500Name("PartyC", "Paris", "FR"))
val NOTARY = TestIdentity(DUMMY_NOTARY_NAME)
