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

package io.onixlabs.corda.core.workflow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.core.utilities.unwrap

/**
 * Sets the current progress tracker step.
 *
 * @param step The progress tracker step.
 * @param log Determines whether to log the step.
 * @param additionalLogInfo Additional information to log when setting the current step.
 */
@Suspendable
fun FlowLogic<*>.currentStep(step: Step, log: Boolean = true, additionalLogInfo: String? = null) {
    progressTracker?.currentStep = step

    if (log) {
        if (additionalLogInfo.isNullOrBlank()) {
            logger.info(step.label)
        } else {
            logger.info("${step.label} ($additionalLogInfo)")
        }
    }
}

/**
 * Finds a recorded transaction in the vault for the specified transaction hash.
 *
 * @param transactionHash The transaction hash of the transaction to find.
 * @return Returns a [SignedTransaction] for the specified transaction hash.
 * @throws FlowException If a signed Transaction cannot be found for the specified transaction hash.
 *
 */
@Suspendable
fun FlowLogic<*>.findTransaction(transactionHash: SecureHash): SignedTransaction {
    return serviceHub.validatedTransactions.getTransaction(transactionHash)
        ?: throw FlowException("Did not find a transaction with the specified hash: $transactionHash")
}

/**
 * Finds a recorded transaction in the vault for the specified transaction hash.
 *
 * @param stateRef The state reference of the transaction to find.
 * @return Returns a [SignedTransaction] for the specified transaction hash.
 * @throws FlowException If a signed Transaction cannot be found for the specified transaction hash.
 *
 */
@Suspendable
fun FlowLogic<*>.findTransaction(stateRef: StateRef): SignedTransaction {
    return findTransaction(stateRef.txhash)
}

/**
 * Finds a recorded transaction in the vault for the specified transaction hash.
 *
 * @param stateAndRef The state and reference of the transaction to find.
 * @return Returns a [SignedTransaction] for the specified transaction hash.
 * @throws FlowException If a signed Transaction cannot be found for the specified transaction hash.
 *
 */
@Suspendable
fun FlowLogic<*>.findTransaction(stateAndRef: StateAndRef<*>): SignedTransaction {
    return findTransaction(stateAndRef.ref)
}

/**
 * Provides a DSL-like transaction builder.
 * To use this function, [BuildTransactionStep] will need to be evident in your progress tracker.
 *
 * @param notary The notary which will be applied to the transaction.
 * @param action The action which will be used to build the transaction.
 * @return Returns a [TransactionBuilder] representing the built transaction.
 */
@Suspendable
fun FlowLogic<*>.buildTransaction(notary: Party, action: TransactionBuilder.() -> Unit): TransactionBuilder {
    currentStep(BuildTransactionStep)
    val transactionBuilder = TransactionBuilder(notary)
    action(transactionBuilder)
    return transactionBuilder
}

/**
 * Verifies a transaction.
 * To use this function, [VerifyTransactionStep] will need to be evident in your progress tracker.
 *
 * @param transaction The transaction to verify.
 */
@Suspendable
fun FlowLogic<*>.verifyTransaction(transaction: TransactionBuilder) {
    currentStep(VerifyTransactionStep)
    transaction.verify(serviceHub)
}

/**
 * Signs a transaction.
 * To use this function, [SignTransactionStep] will need to be evident in your progress tracker.
 */
@Suspendable
fun FlowLogic<*>.signTransaction(transaction: TransactionBuilder): SignedTransaction {
    currentStep(SignTransactionStep)
    val ourSigningKeys = transaction.getOurSigningKeys(serviceHub.keyManagementService)
    return serviceHub.signInitialTransaction(transaction, ourSigningKeys)
}

/**
 * Collects all remaining required signatures from the specified counter-parties.
 * To use this function, [CollectTransactionSignaturesStep] will need to be evident in your progress tracker.
 *
 * Due to the way this function works, it is intended to be paired with [collectSignaturesHandler] in the counter-flow.
 * This function will filter out all required signing sessions from the sessions provided, and will then notify all
 * sessions whether they are required to sign or not. For those sessions required to sign, it will collect their signature.
 *
 * @param transaction The transaction for which to collect remaining signatures from the specified counter-parties.
 * @param sessions All flow sessions that have been passed to this flow.
 * @param additionalSigningAction Allows additional signing actions to be specified. This will be executed after all other activity in this flow.
 * @return Returns a transaction which should be signed by all required signers.
 * @throws FlowException if the local node has been passed to this function as a counter-party or in a flow session.
 */
@Suspendable
inline fun FlowLogic<*>.collectSignatures(
    transaction: SignedTransaction,
    sessions: Iterable<FlowSession>,
    additionalSigningAction: ((SignedTransaction) -> SignedTransaction) = { it }
): SignedTransaction {
    currentStep(CollectTransactionSignaturesStep)
    val missingSigningKeys = transaction.getMissingSigners()

    val signingSessions = sessions.filter {
        if (it.counterparty in serviceHub.myInfo.legalIdentities) {
            throw FlowException("Do not pass flow sessions for the local node.")
        }

        it.counterparty.owningKey in missingSigningKeys
    }

    sessions.forEach { it.send(it in signingSessions) }

    val signedTransaction = if (signingSessions.isEmpty()) transaction else subFlow(
        CollectSignaturesFlow(transaction, signingSessions, CollectTransactionSignaturesStep.childProgressTracker())
    )

    return additionalSigningAction(signedTransaction)
}

/**
 * Signs a transaction.
 * To use this function, [SignTransactionStep] will need to be evident in your progress tracker.
 * Due to the way this function works, it is intended to be paired with [collectSignatures] in the initiating flow.
 *
 * @param session The flow session of the initiating flow that is requesting a transaction signature.
 * @param action Allows custom transaction checks to be performed before the transaction is signed.
 */
@Suspendable
fun FlowLogic<*>.collectSignaturesHandler(
    session: FlowSession,
    action: (SignedTransaction) -> Unit = {}
): SignedTransaction? {
    val isRequiredToSign = session.receive<Boolean>().unwrap { it }
    return if (isRequiredToSign) {
        currentStep(SignTransactionStep)
        subFlow(object : SignTransactionFlow(session, SignTransactionStep.childProgressTracker()) {
            override fun checkTransaction(stx: SignedTransaction) = action(stx)
        })
    } else null
}

/**
 * Finalizes a transaction.
 * To use this function, [SendStatesToRecordStep] and [FinalizeTransactionStep] will need to be evident in your progress tracker.
 *
 * This function allows the initiator to specify how counter-parties should record states of the finalized transaction.
 * For each session of the [statesToRecordBySession] object, the counter-party will receive their [StatesToRecord]
 * enumeration, unless they have specified an override via the [finalizeTransactionHandler] function. Finally they
 * will record the finalized transaction.
 *
 * @param transaction The transaction to finalize and record.
 * @param statesToRecordBySession Determines how counter-parties should record states in the transaction.
 * @param ourStatesToRecord Determines how our node should record states in the transaction.
 * @return Returns a fully signed, finalized and recorded transaction.
 */
@Suspendable
fun FlowLogic<*>.finalizeTransaction(
    transaction: SignedTransaction,
    statesToRecordBySession: StatesToRecordBySession,
    ourStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT
): SignedTransaction {
    return statesToRecordBySession.finalizeTransaction(transaction, this, ourStatesToRecord)
}

/**
 * Finalizes a transaction.
 * To use this function, [SendStatesToRecordStep] and [FinalizeTransactionStep] will need to be evident in your progress tracker.
 *
 * This function allows the initiator to specify how counter-parties should record states of the finalized transaction.
 * Each session will record the transaction states according to the [counterpartyStatesToRecord] parameter, unless they
 * have specified an override via the [finalizeTransactionHandler] function. Finally they will record the finalized transaction.
 *
 * @param transaction The transaction to be finalized.
 * @param sessions The sessions for all counter-parties and observers where this transaction should be recorded.
 * @param counterpartyStatesToRecord Determines how counter-parties should record states in the transaction.
 * @param ourStatesToRecord Determines how our node should record states in the transaction.
 * @return Returns a fully signed, finalized and recorded transaction.
 */
@Suspendable
fun FlowLogic<*>.finalizeTransaction(
    transaction: SignedTransaction,
    sessions: Iterable<FlowSession>,
    counterpartyStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT,
    ourStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT
): SignedTransaction {
    val statesToRecordBySession = StatesToRecordBySession(sessions, counterpartyStatesToRecord)
    return finalizeTransaction(transaction, statesToRecordBySession, ourStatesToRecord)
}

/**
 * Finalizes a transaction.
 * To use this function, [ReceiveStatesToRecordStep] and [RecordFinalizedTransactionStep] will need to be evident in your progress tracker.
 *
 * This function will first receive the [StatesToRecord] from the initiating node, however this can be overridden using
 * the [statesToRecord] parameter. Finally the transaction will be received and recorded.
 *
 * @param session The flow session of the initiating flow that is requesting the transaction to be finalized.
 * @param expectedTransactionId The expected transaction ID of the transaction to be recorded.
 * @param statesToRecord Determines which states from the transaction should be recorded.
 * @return Returns a fully signed, finalized and recorded transaction.
 */
@Suspendable
fun FlowLogic<*>.finalizeTransactionHandler(
    session: FlowSession,
    expectedTransactionId: SecureHash? = null,
    statesToRecord: StatesToRecord? = null
): SignedTransaction {
    currentStep(ReceiveStatesToRecordStep)
    val receivedStatesToRecord = session.receive<StatesToRecord>().unwrap { it }
    val ourStatesToRecord = statesToRecord ?: receivedStatesToRecord

    currentStep(RecordFinalizedTransactionStep)
    return subFlow(ReceiveFinalityFlow(session, expectedTransactionId, ourStatesToRecord))
}
