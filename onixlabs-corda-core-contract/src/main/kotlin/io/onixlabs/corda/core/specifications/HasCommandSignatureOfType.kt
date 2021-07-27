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

package io.onixlabs.corda.core.specifications

import net.corda.core.contracts.CommandData
import java.security.PublicKey

/**
 * Represents a specification that determines whether a command has been signed by the specified signing key.
 *
 * @param signingKey The signing key for which the command should be signed.
 * @param type The command type that should be signed.
 */
class HasCommandSignatureOfType(signingKey: PublicKey, type: Class<out CommandData>) :
    TransactionFunctionSpecification({ signingKey in commandsOfType(type).flatMap { it.signers } })
