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

import net.corda.core.contracts.ContractState
import net.corda.core.flows.FlowSession
import net.corda.core.identity.AbstractParty

/**
 * Obtains a collection of sessions for the specified parties.
 *
 * @param parties The parties for which to obtain flow sessions.
 * @return Returns a filtered collection of flow sessions for the specified parties.
 */
fun Iterable<FlowSession>.sessionsFor(parties: Iterable<AbstractParty>): Set<FlowSession> {
    return filter { it.counterparty in parties }.toSet()
}

/**
 * Obtains a collection of sessions for the specified parties.
 *
 * @param parties The parties for which to obtain flow sessions.
 * @return Returns a filtered collection of flow sessions for the specified parties.
 */
fun Iterable<FlowSession>.sessionsFor(vararg parties: AbstractParty): Set<FlowSession> {
    return sessionsFor(parties.toList())
}

/**
 * Obtains a collection of sessions for the specified state participants.
 *
 * @param states The states for which to obtain flow sessions.
 * @return Returns a filtered collection of flow sessions for the specified state participants.
 */
fun Iterable<FlowSession>.sessionsFor(vararg states: ContractState): Set<FlowSession> {
    return sessionsFor(states.flatMap { it.participants })
}

/**
 * Obtains a session for the specified party.
 *
 * @param party The party for which to obtain a flow session.
 * @return Returns a single flow session for the specified party.
 */
fun Iterable<FlowSession>.sessionFor(party: AbstractParty): FlowSession {
    return sessionsFor(listOf(party)).single()
}

/**
 * Obtains a collection of sessions excluding those for the specified parties.
 *
 * @param parties The parties for which to exclude flow sessions.
 * @return Returns a filtered collection of flow sessions excluding those for the specified parties.
 */
fun Iterable<FlowSession>.sessionsExcluding(parties: Iterable<AbstractParty>): Set<FlowSession> {
    return filterNot { it.counterparty in parties }.toSet()
}

/**
 * Obtains a collection of sessions excluding those for the specified parties.
 *
 * @param parties The parties for which to exclude flow sessions.
 * @return Returns a filtered collection of flow sessions excluding those for the specified parties.
 */
fun Iterable<FlowSession>.sessionsExcluding(vararg parties: AbstractParty): Set<FlowSession> {
    return sessionsExcluding(parties.toList())
}

/**
 * Obtains a collection of sessions excluding those for the specified state participants.
 *
 * @param states The states for which to exclude flow sessions.
 * @return Returns a filtered collection of flow sessions excluding those for the specified state participants.
 */
fun Iterable<FlowSession>.sessionsExcluding(vararg states: ContractState): Set<FlowSession> {
    return sessionsExcluding(states.flatMap { it.participants })
}
