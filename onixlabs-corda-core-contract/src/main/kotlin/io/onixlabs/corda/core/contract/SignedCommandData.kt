package io.onixlabs.corda.core.contract

import net.corda.core.contracts.CommandData
import net.corda.core.crypto.DigitalSignature
import net.corda.core.crypto.isValid
import java.security.PublicKey

/**
 * Defines a contract command that must include a signature.
 *
 * @property signature The signature to include as a payload in the command.
 */
interface SignedCommandData : CommandData {
    val signature: DigitalSignature

    /**
     * Verifies that the signature correlates with the specified content and signing key.
     *
     * @param content The unsigned content to verify against the signature.
     * @param publicKey The public key which was used to sign the content.
     * @return Returns true if the content and public key correlate with the signature; otherwise, false.
     */
    fun isSignedBy(content: ByteArray, publicKey: PublicKey): Boolean {
        return publicKey.isValid(content, signature)
    }
}
