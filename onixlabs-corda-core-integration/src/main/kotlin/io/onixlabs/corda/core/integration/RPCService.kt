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

package io.onixlabs.corda.core.integration

import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps

/**
 * Represents the base class for implementing integration services over RPC.
 *
 * @property rpc The [CordaRPCOps] instance that the service binds to.
 * @property ourIdentity The first legal identity of the local node.
 */
abstract class RPCService(val rpc: CordaRPCOps) {
    protected val ourIdentity: Party get() = rpc.nodeInfo().legalIdentities.first()
}
