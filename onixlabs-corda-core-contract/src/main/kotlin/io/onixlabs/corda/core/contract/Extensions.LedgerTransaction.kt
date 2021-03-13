/**
 * Copyright 2020 Matthew Layton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.onixlabs.corda.core.contract

import net.corda.core.contracts.Command
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.transactions.LedgerTransaction

/**
 * Obtains a single input ref from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @param contractStateClass The class of the contract state type to obtain.
 * @return Returns a single input ref from a ledger transaction of the specified type.
 */
fun <T : ContractState> LedgerTransaction.singleInputRefOfType(contractStateClass: Class<T>): StateAndRef<T> {
    return inRefsOfType(contractStateClass).single()
}

/**
 * Obtains a single input ref from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @return Returns a single input ref from a ledger transaction of the specified type.
 */
inline fun <reified T : ContractState> LedgerTransaction.singleInputRefOfType(): StateAndRef<T> {
    return singleInputRefOfType(T::class.java)
}

/**
 * Obtains a single input from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @param contractStateClass The class of the contract state type to obtain.
 * @return Returns a single input from a ledger transaction of the specified type.
 */
fun <T : ContractState> LedgerTransaction.singleInputOfType(contractStateClass: Class<T>): T {
    return inputsOfType(contractStateClass).single()
}

/**
 * Obtains a single input from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @return Returns a single input from a ledger transaction of the specified type.
 */
inline fun <reified T : ContractState> LedgerTransaction.singleInputOfType(): T {
    return singleInputOfType(T::class.java)
}

/**
 * Obtains a single reference input ref from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @param contractStateClass The class of the contract state type to obtain.
 * @return Returns a single reference input ref from a ledger transaction of the specified type.
 */
fun <T : ContractState> LedgerTransaction.singleReferenceInputRefOfType(contractStateClass: Class<T>): StateAndRef<T> {
    return referenceInputRefsOfType(contractStateClass).single()
}

/**
 * Obtains a single reference input ref from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @return Returns a single reference input ref from a ledger transaction of the specified type.
 */
inline fun <reified T : ContractState> LedgerTransaction.singleReferenceInputRefOfType(): StateAndRef<T> {
    return singleReferenceInputRefOfType(T::class.java)
}

/**
 * Obtains a single reference input from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @param contractStateClass The class of the contract state type to obtain.
 * @return Returns a single reference input from a ledger transaction of the specified type.
 */
fun <T : ContractState> LedgerTransaction.singleReferenceInputOfType(contractStateClass: Class<T>): T {
    return referenceInputsOfType(contractStateClass).single()
}

/**
 * Obtains a single reference input from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @return Returns a single reference input from a ledger transaction of the specified type.
 */
inline fun <reified T : ContractState> LedgerTransaction.singleReferenceInputOfType(): T {
    return singleReferenceInputOfType(T::class.java)
}

/**
 * Obtains a single output ref from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @param contractStateClass The class of the contract state type to obtain.
 * @return Returns a single output ref from a ledger transaction of the specified type.
 */
fun <T : ContractState> LedgerTransaction.singleOutputRefOfType(contractStateClass: Class<T>): StateAndRef<T> {
    return outRefsOfType(contractStateClass).single()
}

/**
 * Obtains a single output ref from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @return Returns a single output ref from a ledger transaction of the specified type.
 */
inline fun <reified T : ContractState> LedgerTransaction.singleOutputRefOfType(): StateAndRef<T> {
    return singleOutputRefOfType(T::class.java)
}

/**
 * Obtains a single output from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @param contractStateClass The class of the contract state type to obtain.
 * @return Returns a single output from a ledger transaction of the specified type.
 */
fun <T : ContractState> LedgerTransaction.singleOutputOfType(contractStateClass: Class<T>): T {
    return outputsOfType(contractStateClass).single()
}

/**
 * Obtains a single output from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @return Returns a single output from a ledger transaction of the specified type.
 */
inline fun <reified T : ContractState> LedgerTransaction.singleOutputOfType(): T {
    return singleOutputOfType(T::class.java)
}

/**
 * Obtains a single command from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @param commandClass The class of the command type to obtain.
 * @return Returns a single command from a ledger transaction of the specified type.
 */
fun <T : CommandData> LedgerTransaction.singleCommandOfType(commandClass: Class<T>): Command<T> {
    return commandsOfType(commandClass).single()
}

/**
 * Obtains a single command from a ledger transaction.
 *
 * @param T The underlying contract state type to obtain.
 * @return Returns a single command from a ledger transaction of the specified type.
 */
inline fun <reified T : CommandData> LedgerTransaction.singleCommandOfType(): Command<T> {
    return singleCommandOfType(T::class.java)
}
