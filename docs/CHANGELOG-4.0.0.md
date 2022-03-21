![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onixlabs-website/main/src/assets/images/logo/full/original/original-md.png)

# Change Log - Version 4.0.0

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

This release supports major features in the ONIXLabs Corda Identity Framework, and the ONIXLabs Corda Business Network Management System.

---

### strictEquals _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Guarantees strict equality between the current `AbstractParty` and the other `AbstractParty`.

```kotlin
fun AbstractParty.strictEquals(other: AbstractParty): Boolean
```

---

### StatePosition _Enum Class_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Determines the position of a state within a transaction.

```kotlin
enum class StatePosition
```

#### Entries

| Name          | Description                                                  |
| ------------- | ------------------------------------------------------------ |
| **INPUT**     | Defines that a resolvable should resolve from a transaction input state. |
| **OUTPUT**    | Defines that a resolvable should resolve from a transaction output state. |
| **REFERENCE** | Defines that a resolvable should resolve from a transaction reference input state. |

#### Remarks

This class replaces `TransactionResolution` which has been part of the API since version 1.0.0, and includes new functionality to resolve input, output and reference states from a `LedgerTransaction` instance.

---

### checkSufficientSessionsForCounterparties _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Checks that sufficient flow sessions have been provided for the specified counter-parties.

```kotlin
@Suspendable
fun FlowLogic<*>.checkSufficientSessionsForCounterparties(
    sessions: Iterable<FlowSession>,
    counterparties: Iterable<AbstractParty>,
    projectParty: (AbstractParty) -> AbstractParty = { it }
): Unit
```

---

### checkSufficientSessionsForContractStates _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Checks that sufficient flow sessions have been provided for the specified states.

```kotlin
@Suspendable
fun FlowLogic<*>.checkSufficientSessionsForContractStates(
    sessions: Iterable<FlowSession>,
    states: Iterable<ContractState>,
    partyProjection: (AbstractParty) -> AbstractParty = { it }
): Unit
```

---

### checkSufficientSessionsForContractStates _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Checks that sufficient flow sessions have been provided for the specified states.

```kotlin
@Suspendable
fun FlowLogic<*>.checkSufficientSessionsForContractStates(
    sessions: Iterable<FlowSession>,
    vararg states: ContractState,
    projectParty: (AbstractParty) -> AbstractParty = { it }
): Unit
```

---

### checkSufficientSessionsForTransactionBuilder _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Checks that sufficient flow sessions have been provided for the specified transaction.

```kotlin
@Suspendable
fun FlowLogic<*>.checkSufficientSessionsForTransactionBuilder(
    sessions: Iterable<FlowSession>,
    transaction: TransactionBuilder,
    partyProjection: (AbstractParty) -> AbstractParty = { it }
    projectParty: (AbstractParty) -> AbstractParty = { it }
): Unit
```

---

