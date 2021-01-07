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
