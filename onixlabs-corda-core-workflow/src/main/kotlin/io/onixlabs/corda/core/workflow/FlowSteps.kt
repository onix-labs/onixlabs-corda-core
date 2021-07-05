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
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step

/**
 * Represents a progress tracker step indicating that a flow is being initialized.
 */
object InitializingFlowStep : Step("Initializing flow.")

/**
 * Represents a progress tracker step indicating that a transaction is being built.
 */
object BuildingTransactionStep : Step("Building transaction.")

/**
 * Represents a progress tracker step indicating that a transaction is is being verified.
 */
object VerifyingTransactionStep : Step("Verifying transaction.")

/**
 * Represents a progress tracker step indicating that a transaction is being signed.
 */
object SigningTransactionStep : Step("Signing transaction.") {
    override fun childProgressTracker(): ProgressTracker = SignTransactionFlow.tracker()
}

/**
 * Represents a progress tracker step indicating that a transaction is being counter-signed.
 */
object CollectTransactionSignaturesStep : Step("Collecting counter-party signatures.") {
    override fun childProgressTracker(): ProgressTracker = CollectSignaturesFlow.tracker()
}

/**
 * Represents a progress tracker step indicating that a transaction is being finalized and recorded.
 */
object FinalizingTransactionStep : Step("Finalizing transaction.") {
    override fun childProgressTracker(): ProgressTracker = FinalityFlow.tracker()
}

/**
 * Represents a progress tracker step indicating that a transaction is being recorded.
 */
object RecordingFinalizedTransactionStep : Step("Recording finalized transaction.")
