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

import net.corda.core.transactions.LedgerTransaction

/**
 * Represents the base class for implementing functional specifications over ledger transactions.
 *
 * @property function The function to execute in order to satisfy the specification.
 */
abstract class TransactionFunctionSpecification(
    private val function: LedgerTransaction.() -> Boolean
) : TransactionSpecification() {

    /**
     * Determines whether the specification is satisfied by the ledger transaction.
     *
     * @param transaction The ledger transaction for which to determine this specification is satisfied.
     * @return Returns true if this specification is satisfied by the specified ledger transaction; otherwise, false.
     */
    final override fun isSatisfiedBy(transaction: LedgerTransaction): Boolean {
        return function(transaction)
    }
}
