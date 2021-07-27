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

import net.corda.core.contracts.ContractState

/**
 * Represents a specification that determines whether a transaction contains the specified reference type.
 *
 * @param type The contract state type that must be used as an reference in the transaction.
 */
class HasReferencesOfTypeSpecification(type: Class<out ContractState>) :
    TransactionFunctionSpecification({ referenceInputsOfType(type).isNotEmpty() })
