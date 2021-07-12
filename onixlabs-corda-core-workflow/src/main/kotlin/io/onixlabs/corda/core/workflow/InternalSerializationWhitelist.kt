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

import net.corda.core.node.StatesToRecord
import net.corda.core.serialization.SerializationWhitelist

/**
 * Represents an internal list of classes to be whitelisted for serialization.
 */
internal class InternalSerializationWhitelist : SerializationWhitelist {
    override val whitelist: List<Class<*>> = listOf(StatesToRecord::class.java)
}