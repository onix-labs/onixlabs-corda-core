package io.onixlabs.corda.core.contract

import net.corda.core.crypto.Crypto
import net.corda.core.crypto.DigitalSignature
import net.corda.core.crypto.sign
import net.corda.core.node.ServiceHub
import net.corda.core.serialization.CordaSerializable
import net.corda.core.utilities.toBase64
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

/**
 * Represents an array of unsigned bytes, and its signed equivalent.
 *
 * @property content The unsigned signature content.
 * @property signature The digital signature representing the signed content.
 */
@CordaSerializable
data class SignatureData(private val content: ByteArray, private val signature: DigitalSignature) {

    companion object {
        fun create(content: ByteArray, privateKey: PrivateKey): SignatureData {
            val signature = privateKey.sign(content)
            return SignatureData(content, signature)
        }

        fun create(content: ByteArray, publicKey: PublicKey, serviceHub: ServiceHub): SignatureData {
            val signature = serviceHub.keyManagementService.sign(content, publicKey)
            return SignatureData(content, signature.withoutKey())
        }
    }

    /**
     * Verifies the signature data using the specified public key.
     *
     * @param publicKey The public key to verify against the signature.
     * @return Returns true if the public key was used to sign the data; otherwise, false.
     */
    fun verify(publicKey: PublicKey): Boolean {
        return Crypto.isValid(publicKey, signature.bytes, content)
    }

    /**
     * Determines whether the specified object is equal to the current object.
     *
     * @param other The object to compare with the current object.
     * @return Returns true if the specified object is equal to the current object; otherwise, false.
     */
    override fun equals(other: Any?): Boolean {
        return this === other || (other is SignatureData
                && content.contentEquals(other.content)
                && signature == other.signature)
    }

    /**
     * Serves as the default hash function.
     *
     * @return Returns a hash code for the current object.
     */
    override fun hashCode(): Int {
        return Objects.hash(signature, content.contentHashCode())
    }

    /**
     * Returns a string that represents the current object.
     *
     * @return Returns a string that represents the current object.
     */
    override fun toString(): String = buildString {
        appendln("Unsigned bytes: ${content.toBase64()}")
        appendln("Signed bytes: ${signature.bytes.toBase64()}")
    }
}
