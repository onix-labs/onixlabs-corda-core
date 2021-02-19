package io.onixlabs.corda.core.contract

import net.corda.core.contracts.ContractClassName

/**
 * Defines an interface which automatically binds a contract ID to a contract class.
 *
 * @property ID The ID of the contract.
 */
interface ContractID {
    val ID: ContractClassName get() = this::class.java.enclosingClass.canonicalName
}
