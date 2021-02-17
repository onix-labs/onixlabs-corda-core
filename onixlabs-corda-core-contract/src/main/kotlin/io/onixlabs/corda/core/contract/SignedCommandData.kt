package io.onixlabs.corda.core.contract

import net.corda.core.contracts.CommandData

/**
 * Defines a contract command that must include a signature.
 *
 * @property signature The signature to include as a payload in the command.
 */
interface SignedCommandData : CommandData {
    val signature: SignatureData
}
