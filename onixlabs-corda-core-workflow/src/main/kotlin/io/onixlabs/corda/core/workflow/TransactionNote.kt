package io.onixlabs.corda.core.workflow

import net.corda.core.crypto.SecureHash
import net.corda.core.serialization.CordaSerializable

/**
 * Represents a transaction note.
 *
 * @property transactionId The ID of the transaction that the note should be attached to.
 * @property text The transaction note text.
 */
@CordaSerializable
data class TransactionNote(val transactionId: SecureHash, val text: String)