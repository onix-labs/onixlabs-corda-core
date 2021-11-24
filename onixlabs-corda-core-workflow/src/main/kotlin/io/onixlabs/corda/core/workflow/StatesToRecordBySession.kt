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
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker

/**
 * Represents a mapping of [FlowSession] to [StatesToRecord],
 * allowing a transaction initiator to specify how each counter-party should record the states of a transaction.
 *
 * @param statesToRecordBySession A map of [StatesToRecord] by [FlowSession].
 * @property sessions The flow sessions of the underlying map.
 */
class StatesToRecordBySession(statesToRecordBySession: Map<FlowSession, StatesToRecord> = emptyMap()) {

    /**
     * Creates a new instance of the [StatesToRecordBySession] class.
     *
     * @param sessions The flow sessions to add to the underlying map.
     * @param statesToRecord The default states to record for each flow session.
     */
    constructor(
        sessions: Iterable<FlowSession>,
        statesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT
    ) : this(sessions.map { it to statesToRecord }.toMap())

    val sessions: Set<FlowSession> get() = mutableStatesToRecordBySession.keys
    private val mutableStatesToRecordBySession = statesToRecordBySession.toMutableMap()

    /**
     * Sets the [StatesToRecord] for the specified [FlowSession].
     * If a flow session already exists in the underlying map, this function will overwrite its [StatesToRecord] value.
     *
     * @param session The session to add to the underlying map.
     * @param statesToRecord The [StatesToRecord] value for the specified flow session.
     */
    fun setSessionStatesToRecord(session: FlowSession, statesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT) {
        mutableStatesToRecordBySession[session] = statesToRecord
    }

    /**
     * Adds and sets the [StatesToRecord] for the specified [FlowSession].
     * If a flow session already exists in the underlying map, this function will leave its [StatesToRecord] value intact.
     *
     * @param session The session to add to the underlying map.
     * @param statesToRecord The [StatesToRecord] value for the specified flow session.
     */
    fun addSessionStatesToRecord(session: FlowSession, statesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT) {
        if (session !in sessions) setSessionStatesToRecord(session, statesToRecord)
    }

    /**
     * Finalizes the transaction.
     *
     * @param transaction The transaction to finalize and record.
     * @param flowLogic The flow logic for the flow that is initiating the transaction.
     * @param ourStatesToRecord Specifies how our node should record the states in the transaction.
     * @param childProgressTracker Specifies the child progress tracker for the finality flow.
     * @return Returns a fully signed, finalized and recorded transaction.
     */
    @Suspendable
    internal fun finalizeTransaction(
        transaction: SignedTransaction,
        flowLogic: FlowLogic<*>,
        ourStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT,
        childProgressTracker: ProgressTracker = FinalizeTransactionStep.childProgressTracker()
    ): SignedTransaction {
        if (mutableStatesToRecordBySession.isNotEmpty()) {
            flowLogic.currentStep(SendStatesToRecordStep)
            mutableStatesToRecordBySession.forEach { (key, value) -> key.send(value.name) }
        }

        flowLogic.currentStep(FinalizeTransactionStep)
        return flowLogic.subFlow(FinalityFlow(transaction, sessions, ourStatesToRecord, childProgressTracker))
    }
}
