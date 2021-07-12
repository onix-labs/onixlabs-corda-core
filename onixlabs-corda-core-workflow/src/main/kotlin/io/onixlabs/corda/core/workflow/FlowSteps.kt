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

import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.node.StatesToRecord
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step

/**
 * Represents a progress tracker step indicating that a flow is being initialized.
 */
object InitializeFlowStep : Step("Initializing flow.")

/**
 * Represents a progress tracker step indicating that a transaction is being built.
 */
object BuildTransactionStep : Step("Building transaction.")

/**
 * Represents a progress tracker step indicating that a transaction is being verified.
 */
object VerifyTransactionStep : Step("Verifying transaction.")

/**
 * Represents a progress tracker step indicating that a transaction is being signed.
 */
object SignTransactionStep : Step("Signing transaction.") {
    override fun childProgressTracker(): ProgressTracker = SignTransactionFlow.tracker()
}

/**
 * Represents a progress tracker step indicating that a transaction is being counter-signed.
 */
object CollectTransactionSignaturesStep : Step("Collecting counter-party signatures.") {
    override fun childProgressTracker(): ProgressTracker = CollectSignaturesFlow.tracker()
}

/**
 * Represents a progress tracker step indicating that that [StatesToRecord] is being send to a counter-party.
 */
object SendStatesToRecordStep : Step("Sending states to record to counter-party.")

/**
 * Represents a progress tracker step indicating that that [StatesToRecord] is being received from a counter-party.
 */
object ReceiveStatesToRecordStep : Step("Receiving states to record from counter-party.")

/**
 * Represents a progress tracker step indicating that a transaction is being finalized and recorded.
 */
object FinalizeTransactionStep : Step("Finalizing transaction.") {
    override fun childProgressTracker(): ProgressTracker = FinalityFlow.tracker()
}

/**
 * Represents a progress tracker step indicating that a transaction is being recorded.
 */
object RecordFinalizedTransactionStep : Step("Recording finalized transaction.")
