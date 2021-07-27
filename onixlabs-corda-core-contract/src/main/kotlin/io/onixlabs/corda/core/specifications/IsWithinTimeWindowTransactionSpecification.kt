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

import java.time.Instant

/**
 * Represents a specification that determines whether the specified instant is within the transaction time window.
 *
 * @param instant The instant to determine is within the transaction time window.
 * @param allowNull Determines whether a null time window should satisfy the transaction.
 */
class IsWithinTimeWindowTransactionSpecification(instant: Instant, allowNull: Boolean = false) :
    TransactionFunctionSpecification({ timeWindow?.contains(instant) ?: allowNull })
