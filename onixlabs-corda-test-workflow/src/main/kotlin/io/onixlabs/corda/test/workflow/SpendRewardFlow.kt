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

package io.onixlabs.corda.test.workflow

import co.paralleluniverse.fibers.Suspendable
import io.onixlabs.corda.core.contract.SignatureData
import io.onixlabs.corda.core.contract.owningKeys
import io.onixlabs.corda.core.contract.resolveOrThrow
import io.onixlabs.corda.core.workflow.*
import io.onixlabs.corda.test.contract.Reward
import io.onixlabs.corda.test.contract.RewardContract
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.hash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker

class SpendRewardFlow(
    private val reward: StateAndRef<Reward>,
    private val notary: Party,
    private val sessions: Set<FlowSession> = emptySet(),
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<SignedTransaction>() {

    companion object {
        @JvmStatic
        fun tracker() = ProgressTracker(
            InitializeFlowStep,
            BuildTransactionStep,
            VerifyTransactionStep,
            SignTransactionStep,
            CollectTransactionSignaturesStep,
            SendStatesToRecordStep,
            FinalizeTransactionStep
        )
    }

    @Suspendable
    override fun call(): SignedTransaction {
        currentStep(InitializeFlowStep)
        val rewardState = reward.state.data
        val customer = rewardState.customer.resolveOrThrow(serviceHub)
        val signature = SignatureData.create(rewardState.hash().bytes, rewardState.owner.owningKey, serviceHub)

        val transaction = buildTransaction(notary) {
            addInputState(reward)
            addReferenceState(customer.referenced())
            addCommand(RewardContract.Spend(signature), rewardState.participants.owningKeys.toList())
        }

        verifyTransaction(transaction)
        val partiallySignedTransaction = signTransaction(transaction)
        val fullySignedTransaction = collectSignatures(partiallySignedTransaction, sessions)
        return finalizeTransaction(fullySignedTransaction, sessions)
    }

    @StartableByRPC
    @StartableByService
    @InitiatingFlow(version = 1)
    class Initiator(
        private val reward: StateAndRef<Reward>,
        private val notary: Party? = null,
        private val observers: Set<Party> = emptySet()
    ) : FlowLogic<SignedTransaction>() {

        private companion object {
            object SpendingRewardStep : ProgressTracker.Step("Spending reward.") {
                override fun childProgressTracker() = tracker()
            }
        }

        override val progressTracker = ProgressTracker(SpendingRewardStep)

        @Suspendable
        override fun call(): SignedTransaction {
            currentStep(SpendingRewardStep)
            return subFlow(
                SpendRewardFlow(
                    reward,
                    notary ?: getPreferredNotary { firstNotary },
                    initiateFlows(observers, reward.state.data),
                    SpendingRewardStep.childProgressTracker()
                )
            )
        }
    }
}
