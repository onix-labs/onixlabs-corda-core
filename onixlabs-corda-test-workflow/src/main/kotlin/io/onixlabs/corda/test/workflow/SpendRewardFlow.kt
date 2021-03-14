/**
 * Copyright 2020-2021 Matthew Layton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import io.onixlabs.corda.core.workflow.firstNotary
import io.onixlabs.corda.core.workflow.getPreferredNotary
import io.onixlabs.corda.core.workflow.initiateFlows
import io.onixlabs.corda.test.contract.Reward
import io.onixlabs.corda.test.contract.RewardContract
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.hash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

class SpendRewardFlow(
    private val reward: StateAndRef<Reward>,
    private val notary: Party,
    private val sessions: Set<FlowSession> = emptySet()
) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val rewardState = reward.state.data
        val customer = rewardState.customer.resolveOrThrow(serviceHub)
        val signature = SignatureData.create(rewardState.hash().bytes, rewardState.owner.owningKey, serviceHub)

        val transaction = with(TransactionBuilder(notary)) {
            addInputState(reward)
            addReferenceState(customer.referenced())
            addCommand(RewardContract.Spend(signature), rewardState.participants.owningKeys.toList())
        }

        transaction.verify(serviceHub)
        val partiallySignedTransaction = serviceHub.signInitialTransaction(transaction, rewardState.owner.owningKey)

        sessions.forEach { it.send(it.counterparty.owningKey in rewardState.participants.owningKeys) }
        val signingSessions = sessions.filter { it.counterparty.owningKey in rewardState.participants.owningKeys }
        val fullySignedTransaction = subFlow(CollectSignaturesFlow(partiallySignedTransaction, signingSessions))

        return subFlow(FinalityFlow(fullySignedTransaction, sessions))
    }

    @StartableByRPC
    @StartableByService
    @InitiatingFlow(version = 1)
    class Initiator(
        private val reward: StateAndRef<Reward>,
        private val notary: Party? = null,
        private val observers: Set<Party> = emptySet()
    ) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            val sessions = initiateFlows(observers, reward.state.data)
            return subFlow(
                SpendRewardFlow(
                    reward,
                    notary ?: getPreferredNotary { firstNotary },
                    sessions
                )
            )
        }
    }
}
