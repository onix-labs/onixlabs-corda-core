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

package io.onixlabs.corda.test.workflow

import co.paralleluniverse.fibers.Suspendable
import io.onixlabs.corda.core.workflow.ReceiveStatesToRecordStep
import io.onixlabs.corda.core.workflow.RecordFinalizedTransactionStep
import io.onixlabs.corda.core.workflow.currentStep
import io.onixlabs.corda.core.workflow.finalizeTransactionHandler
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker

class AmendCustomerFlowHandler(
    private val session: FlowSession,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<SignedTransaction>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(ReceiveStatesToRecordStep, RecordFinalizedTransactionStep)
    }

    @Suspendable
    override fun call(): SignedTransaction {
        return finalizeTransactionHandler(session, statesToRecord = StatesToRecord.ALL_VISIBLE)
    }

    @InitiatedBy(AmendCustomerFlow.Initiator::class)
    private class Handler(private val session: FlowSession) : FlowLogic<SignedTransaction>() {

        private companion object {
            object ObservingAmendedCustomerStep : ProgressTracker.Step("Observing amended customer.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(ObservingAmendedCustomerStep)

        @Suspendable
        override fun call(): SignedTransaction {
            currentStep(ObservingAmendedCustomerStep)
            return subFlow(AmendCustomerFlowHandler(session, ObservingAmendedCustomerStep.childProgressTracker()))
        }
    }
}
