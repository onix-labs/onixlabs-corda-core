/**
 * Copyright 2020-2021 Matthew Layton
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

package io.onixlabs.corda.test.contract

import net.corda.core.node.services.vault.CriteriaExpression
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.schemas.StatePersistable

fun <T : StatePersistable> QueryCriteria.VaultQueryCriteria.andWithExpressions(
    vararg expressions: CriteriaExpression<T, Boolean>?
): QueryCriteria {
    val nonNullExpressions = expressions.filterNotNull()
    return if (nonNullExpressions.isEmpty()) this else and(expressions
        .filterNotNull()
        .map { QueryCriteria.VaultCustomQueryCriteria(it, status, contractStateTypes, relevancyStatus) }
        .reduce<QueryCriteria, QueryCriteria> { a, b -> a.and(b) }
    )
}