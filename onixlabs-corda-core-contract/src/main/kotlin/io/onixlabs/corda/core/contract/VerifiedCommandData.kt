package io.onixlabs.corda.core.contract

import net.corda.core.contracts.CommandData
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

/**
 * Defines a contract command that can verify a ledger transaction.
 */
interface VerifiedCommandData : CommandData {

    /**
     * Verifies a ledger transaction.
     *
     * @param transaction The ledger transaction to verify.
     * @param signers The list of signers expected to sign the transaction.
     */
    fun verify(transaction: LedgerTransaction, signers: Set<PublicKey>)
}
