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

import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.LedgerTransaction

/**
 * Represents the base class for implementing specifications over ledger transactions.
 */
@CordaSerializable
abstract class TransactionSpecification {

    companion object {

        /**
         * Creates an empty transaction specification which is always satisfied.
         *
         * @return Returns an empty transaction specification which is always satisfied.
         */
        fun create(): TransactionSpecification {
            return EmptyTransactionSpecification()
        }
    }

    /**
     * Determines whether the specification is satisfied by the ledger transaction.
     *
     * @param transaction The ledger transaction for which to determine this specification is satisfied.
     * @return Returns true if this specification is satisfied by the specified ledger transaction; otherwise, false.
     */
    abstract fun isSatisfiedBy(transaction: LedgerTransaction): Boolean

    /**
     * Creates a logical AND of this specification and the specified other specification.
     *
     * @param other The other specification to logically AND with this specification.
     * @return Returns a logical AND of this specification and the specified other specification.
     */
    fun and(other: TransactionSpecification): TransactionSpecification {
        return AndTransactionSpecification(other, this)
    }

    /**
     * Creates a logical OR of this specification and the specified other specification.
     *
     * @param other The other specification to logically OR with this specification.
     * @return Returns a logical OR of this specification and the specified other specification.
     */
    fun or(other: TransactionSpecification): TransactionSpecification {
        return OrTransactionSpecification(other, this)
    }

    /**
     * Creates a logical NOT, or negation of this specification.
     *
     * @return Returns a logical NOT, or negation of this specification.
     */
    fun not(): TransactionSpecification {
        return NotTransactionSpecification(this)
    }

    /**
     * Represents a logical AND of two specifications.
     *
     * @param left The left specification to logically AND together.
     * @param right The right specification to logically AND together.
     */
    private class AndTransactionSpecification(left: TransactionSpecification, right: TransactionSpecification) :
        TransactionFunctionSpecification({ left.isSatisfiedBy(this) && right.isSatisfiedBy(this) })

    /**
     * Represents a logical OR of two specifications.
     *
     * @param left The left specification to logically OR together.
     * @param right The right specification to logically OR together.
     */
    private class OrTransactionSpecification(left: TransactionSpecification, right: TransactionSpecification) :
        TransactionFunctionSpecification({ left.isSatisfiedBy(this) || right.isSatisfiedBy(this) })

    /**
     * Represents a logical NOT, or negation of a specification.
     *
     * @param specification The specification to logically NOT, or negate.
     */
    private class NotTransactionSpecification(specification: TransactionSpecification) :
        TransactionFunctionSpecification({ !specification.isSatisfiedBy(this) })

    /**
     * Represents an empty specification that is always satisfied.
     */
    private class EmptyTransactionSpecification : TransactionFunctionSpecification({ true })
}
