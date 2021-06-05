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

import net.corda.core.crypto.SecureHash
import net.corda.core.serialization.CordaSerializable

/**
 * Represents a transaction note.
 *
 * @property transactionId The ID of the transaction that the note should be attached to.
 * @property text The transaction note text.
 */
@CordaSerializable
data class TransactionNote(val transactionId: SecureHash, val text: String)
