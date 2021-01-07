package io.onixlabs.corda.core.integration

import io.onixlabs.corda.core.workflow.Message
import io.onixlabs.corda.core.workflow.MessageAcknowledgement
import io.onixlabs.corda.core.workflow.SendMessageFlow
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.FlowProgressHandle
import net.corda.core.messaging.StateMachineUpdate
import net.corda.core.messaging.startTrackedFlow
import java.util.function.Consumer

class MessageService(rpc: CordaRPCOps) : RPCService(rpc) {

    fun <T> sendMessage(
        message: T,
        counterparties: Set<Party>
    ): FlowProgressHandle<Map<Party, MessageAcknowledgement>> where T : Message<*> {
        return rpc.startTrackedFlow { SendMessageFlow.Initiator(message, counterparties) }
    }

    inline fun <reified T> subscribe(
        crossinline onSuccess: (message: T) -> Unit = {},
        crossinline onFailure: (error: Throwable) -> Unit = {}
    ) where T : Message<*> {
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
