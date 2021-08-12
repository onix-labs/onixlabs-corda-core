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
    logger.info("Using the specified CorDapp for notary selection: ${cordappContext.cordapp.name}")
    return if (serviceHub.getAppContext().config.exists("notary")) {
        val name = CordaX500Name.parse(serviceHub.getAppContext().config.getString("notary"))
        serviceHub.networkMapCache.getNotary(name) ?: throw IllegalArgumentException(
            "Notary with the specified name cannot be found in the network map cache: $name."
        )
    } else defaultSelector(serviceHub)
}
