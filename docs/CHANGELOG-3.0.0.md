![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log - Version 3.0.0

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

This release publishes source code along with the compiled dependencies, enabling developers debug into the ONIXLabs APIs and understand what's going on under the hood.

---

### sortAndReduce _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Sorts and reduces an `Iterable` of `SecureHash` into a single hash.

```kotlin
fun Iterable<SecureHash>.sortAndReduce(): SecureHash
```

---

### SignatureData *Class*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Represents an array of unsigned bytes, and its signed equivalent.

```kotlin
@CordaSerializable
data class SignatureData(
    val content: ByteArray, 
    val signature: DigitalSignature
)
```

>   🔵  **INFORMATION**
>
>   This API exists in version 1.2.0 however in version 3.0.0 the `verify` function no longer returns `Boolean`. Instead, the `isValid` function replaces the `verify` function, and the `verify` function now returns `Unit`, or throws a `SignatureException` if the digital signature could not be verified by the specified public key.

---

### SendTransactionStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a signed transaction is being sent.

```kotlin
object SendTransactionStep : Step
```

### Remarks

The `publishTransaction` extension function uses this as the default `progressTrackerStep` argument, however you can replace it with your own progress tracker step.

---

### ReceiveTransactionStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a signed transaction is being received.

```kotlin
object SendTransactionStep : Step
```

### Remarks

The `publishTransactionHandler` extension function uses this as the default `progressTrackerStep` argument, however you can replace it with your own progress tracker step.

---

### publishTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Publishes a `SignedTransaction` to the specified flow sessions.

```kotlin
@Suspendable
fun FlowLogic<*>.publishTransaction(
    transaction: SignedTransaction,
    sessions: Set<FlowSession>,
    progressTrackerStep: Step = SendTransactionStep
): SignedTransaction
```

#### Remarks

To use this function, `SendTransactionStep` will need to be evident in your progress tracker, unless you replace the default argument with your own progress tracker step.

---

### publishTransactionHandler _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Handles and records a published `SignedTransaction`.

```kotlin
@Suspendable
fun FlowLogic<*>.publishTransactionHandler(
    session: FlowSession,
    statesToRecord: StatesToRecord = StatesToRecord.ALL_VISIBLE,
    checkSufficientSignatures: Boolean = true,
    progressTrackerStep: Step = ReceiveTransactionStep
): SignedTransaction
```

#### Remarks

To use this function, `ReceiveTransactionStep` will need to be evident in your progress tracker, unless you replace the default argument with your own progress tracker step.

---

### finalizeTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Finalizes a transaction.

```kotlin
@Suspendable
fun FlowLogic<*>.finalizeTransaction(
    transaction: SignedTransaction,
    sessions: Iterable<FlowSession> = emptyList(),
    counterpartyStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT,
    ourStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT
): SignedTransaction
```

>   🔵  **INFORMATION**
>
>   This API exists in version 2.1.0 however in version 3.0.0 there are two notable changes:
>
>   1.   The `sessions` parameter provides a default `emptyList()` argument; useful for finalising local transactions.
>   2.   `SendStatesToRecordStep` only needs to be evident in your progress tracker when finalising transactions with counter-parties. For local transactions, this progress tracker step can be omitted.

---

### StatesToRecordBySession _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a mapping of `FlowSession` to `StatesToRecord`, allowing a transaction initiator to specify how each counter-party should record the states of a transaction.
```kotlin
class StatesToRecordBySession(
  statesToRecordBySession: Map<FlowSession, StatesToRecord> = emptyMap()
)
```

### Remarks

>   🔵  **INFORMATION**
>
>   This API exists in version 2.1.0 however in version 3.0.0, there are two notable changes:
>
>   1.   `addSession` has been renamed to `setSessionStatesToRecord`. If a flow session already exists in the underlying map, this function will overwrite its `StatesToRecord` value. If a flow session doesn't exist in the underlying map, this function will add the flow session to the underlying map with the specified `StatesToRecord` value.
>   2.   `addMissionSession` has been renamed to `addSessionStatesToRecord`. If a flow session already exists in the underlying map, this function will ignore the addition.  If a flow session doesn't exist in the underlying map, this function will add the flow session to the underlying map with the specified `StatesToRecord` value.

---

### 
