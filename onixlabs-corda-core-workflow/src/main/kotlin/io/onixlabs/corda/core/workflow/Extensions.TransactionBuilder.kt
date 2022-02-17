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

package io.onixlabs.corda.core.workflow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.identity.AbstractParty
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.KeyManagementService
import net.corda.core.transactions.TransactionBuilder
import java.security.PublicKey

/**
 * Gets all of the required signing keys from the transaction builder.
 */
val TransactionBuilder.requiredSigningKeys: List<PublicKey> get() = commands().flatMap { it.signers }.distinct()

/**
 * Gets a list of our required signing keys for this transaction.
 *
 * @param keyManagementService The key management service from which to resolve signing keys.
 * @return Returns a list of our required signing keys for this transaction.
 */
@Suspendable
fun TransactionBuilder.getOurSigningKeys(keyManagementService: KeyManagementService): List<PublicKey> {
    return requiredSigningKeys.filter { it in keyManagementService.ourKeys }.distinct()
}

/**
 * Gets a list of required counter-party signing keys for this transaction.
 *
 * @param keyManagementService The key management service from which to resolve signing keys.
 * @return Returns a list of required counter-party signing keys for this transaction.
 */
fun TransactionBuilder.getCounterpartySigningKeys(keyManagementService: KeyManagementService): List<PublicKey> {
    return requiredSigningKeys.filterNot { it in keyManagementService.ourKeys }.distinct()
}

/**
 * Gets all of the transaction participants for this transaction.
 *
 * @param serviceHub The service hub from which to resolve the transaction participants.
 * @return Returns a list containing all of the transaction participants for this transaction.
 */
fun TransactionBuilder.getTransactionParticipants(serviceHub: ServiceHub): List<AbstractParty> {
    val transaction = toLedgerTransaction(serviceHub)
    val inputParticipants = transaction.inputStates.flatMap { it.participants }
    val outputParticipants = transaction.outputStates.flatMap { it.participants }
    return (inputParticipants + outputParticipants).distinct()
}
