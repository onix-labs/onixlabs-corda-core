package io.onixlabs.corda.core.integration

import io.onixlabs.corda.core.workflow.SendTransactionNoteFlow
import io.onixlabs.corda.core.workflow.TransactionNote
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.FlowProgressHandle
import net.corda.core.messaging.startTrackedFlow

class TransactionNoteService(rpc: CordaRPCOps) : RPCService(rpc) {

    fun addTransactionNote(
        transactionNote: TransactionNote,
        counterparties: Set<Party>,
        addNoteToTransaction: Boolean = true
    ): FlowProgressHandle<Unit> {
        return rpc.startTrackedFlow {
            SendTransactionNoteFlow.Initiator(
                transactionNote,
                counterparties,
                addNoteToTransaction
            )
        }
    }
}