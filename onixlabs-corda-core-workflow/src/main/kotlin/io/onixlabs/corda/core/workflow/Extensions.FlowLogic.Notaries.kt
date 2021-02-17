package io.onixlabs.corda.core.workflow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.cordapp.CordappContext
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.internal.randomOrNull
import net.corda.core.node.ServiceHub

/**
 * Gets the first available notary.
 */
val FlowLogic<*>.firstNotary: Party
    get() = serviceHub.networkMapCache.notaryIdentities.firstOrNull()
        ?: throw NoSuchElementException("No available notaries.")

/**
 * Gets a randomly available notary.
 */
val FlowLogic<*>.randomNotary: Party
    get() = serviceHub.networkMapCache.notaryIdentities.randomOrNull()
        ?: throw NoSuchElementException("No available notaries.")

/**
 * Gets the preferred notary from the CorDapp config, or alternatively a default notary in the event that
 * a preferred notary has not been specified in the CorDapp config.
 *
 * @param serviceHub The service hub which will be used to obtain a notary from the config file.
 * @param defaultSelector The selector function to obtain a notary if none have been specified in the CorDapp config.
 * @return Returns the preferred or default notary.
 * @throws IllegalArgumentException If the preferred notary cannot be found in the network map cache.
 */
@Suspendable
fun FlowLogic<*>.getPreferredNotary(
    serviceHub: ServiceHub = this.serviceHub,
    defaultSelector: (ServiceHub) -> Party = { firstNotary }
): Party {
    val cordappContext: CordappContext = serviceHub.getAppContext()
    logger.info("Using the specified cordapp for notary selection: ${cordappContext.cordapp.name}")
    return if (serviceHub.getAppContext().config.exists("notary")) {
        val name = CordaX500Name.parse(serviceHub.getAppContext().config.getString("notary"))
        serviceHub.networkMapCache.getNotary(name) ?: throw IllegalArgumentException(
            "Notary with the specified name cannot be found in the network map cache: $name."
        )
    } else defaultSelector(serviceHub)
}
