/*
 * Copyright 2020-2022 ONIXLabs
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

import net.corda.core.serialization.CordaSerializable
import java.util.*

/**
 * Represents a transient message.
 *
 * @param T The underlying type of the data object to be transmitted.
 * @property data The data object to transmit.
 * @property id The identity of the message.
 */
@CordaSerializable
open class Message<T : Any>(val data: T, val id: UUID = UUID.randomUUID())
