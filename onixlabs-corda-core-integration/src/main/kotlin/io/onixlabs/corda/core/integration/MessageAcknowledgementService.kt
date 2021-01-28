/**
 * Copyright 2020 Matthew Layton
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

package io.onixlabs.corda.core.integration

import io.onixlabs.corda.core.workflow.MessageAcknowledgement
import io.onixlabs.corda.core.workflow.SendMessageAcknowledgementFlow
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.FlowProgressHandle
import net.corda.core.messaging.StateMachineUpdate
import net.corda.core.messaging.startTrackedFlow
import java.util.function.Consumer

class MessageAcknowledgementService(rpc: CordaRPCOps) : RPCService(rpc) {

    fun <T> sendMessageAcknowledgement(
        acknowledgement: T,
        counterparty: Party
    ): FlowProgressHandle<Unit> where T : MessageAcknowledgement {
        return rpc.startTrackedFlow { SendMessageAcknowledgementFlow.Initiator(acknowledgement, counterparty) }
    }

    inline fun <reified T> subscribe(
        crossinline onSuccess: (acknowledgement: T) -> Unit = {},
        crossinline onFailure: (error: Throwable) -> Unit = {}
    ) where T : MessageAcknowledgement {
        rpc.stateMachinesFeed().updates.subscribe {
            if (it is StateMachineUpdate.Removed) {
                it.result.doOnSuccess(Consumer { result ->
                    if (result is T) {
                        onSuccess(result)
                    }
                })

                it.result.doOnFailure(Consumer { error ->
                    onFailure(error)
                })
            }
        }
    }
}
