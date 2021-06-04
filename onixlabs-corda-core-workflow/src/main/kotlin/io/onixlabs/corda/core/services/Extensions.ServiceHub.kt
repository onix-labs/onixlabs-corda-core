package io.onixlabs.corda.core.services

import net.corda.core.contracts.ContractState
import net.corda.core.node.ServiceHub

/**
 * Creates a vault service, bound to the specified [ContractState] type.
 *
 * @param T The underlying [ContractState] type to bind the vault service to.
 * @return Returns a [VaultService] instance bound to the underlying [ContractState] type.
 */
inline fun <reified T : ContractState> ServiceHub.vaultServiceFor(): VaultService<T> {
    return VaultService.create(this)
}
