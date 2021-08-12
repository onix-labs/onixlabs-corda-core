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

package io.onixlabs.corda.core.contract

import net.corda.core.crypto.Crypto
import net.corda.core.crypto.DigitalSignature
import net.corda.core.crypto.sign
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.KeyManagementService
import net.corda.core.serialization.CordaSerializable
import net.corda.core.utilities.toBase58String
import net.corda.core.utilities.toBase64
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SignatureException
import java.util.*

/**
 * Represents an array of unsigned bytes, and its signed equivalent.
 *
 * @property content The unsigned signature content.
 * @property signature The digital signature representing the signed content.
 */
@CordaSerializable
data class SignatureData(val content: ByteArray, val signature: DigitalSignature) {

    companion object {

        private const val CANNOT_OBTAIN_PRIVATE_KEY_EXCEPTION =
            "Failed to obtain a private key matching the specified public key from the key management service."

        /**
         * Creates a signature from the specified content and private key.
         *
         * @param content The content to sign.
         * @param privateKey The private key to sign the content.
         * @return Returns a new signature containing the content and signed data.
         */
        fun create(content: ByteArray, privateKey: PrivateKey): SignatureData {
            val signature = privateKey.sign(content)
            return SignatureData(content, signature)
        }

        /**
         * Creates a signature from the specified content by resolving the signing key from the service hub.
         *
         * @param content The content to sign.
         * @param publicKey The public key to resolve from the service hub.
         * @param service The key management service to resolve and sign the signature data.
         * @return Returns a new signature containing the content and signed data.
         */
        fun create(content: ByteArray, publicKey: PublicKey, service: KeyManagementService): SignatureData = try {
            SignatureData(content, service.sign(content, publicKey).withoutKey())
        } catch (ex: NoSuchElementException) {
            throw SignatureException(CANNOT_OBTAIN_PRIVATE_KEY_EXCEPTION, ex)
        }

        /**
         * Creates a signature from the specified content by resolving the signing key from the service hub.
         *
         * @param content The content to sign.
         * @param publicKey The public key to resolve from the service hub.
         * @param serviceHub The service hub to resolve the public key.
         * @return Returns a new signature containing the content and signed data.
         */
        fun create(content: ByteArray, publicKey: PublicKey, serviceHub: ServiceHub): SignatureData {
            return create(content, publicKey, serviceHub.keyManagementService)
        }
    }

    /**
     * Determines whether the signature data was signed by the specified key.
     *
     * @param publicKey The public key to verify against the signature.
     * @return Returns true if the signature data was signed by the specified key; otherwise, false.
     */
    fun isValid(publicKey: PublicKey): Boolean {
        return Crypto.isValid(publicKey, signature.bytes, content)
    }

    /**
     * Verifies the signature data using the specified public key.
     *
     * @param publicKey The public key to verify against the signature.
     * @return Returns true if the public key was used to sign the data; otherwise, false.
     */
    fun verify(publicKey: PublicKey) {
        if (!isValid(publicKey)) {
            throw SignatureException("Signature was not signed by the specified key: ${publicKey.toBase58String()}")
        }
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
