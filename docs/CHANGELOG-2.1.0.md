![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log - Version 2.1.0

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

This release contains several API improvements for workflow design and composability. Typically when writing flows, we find ourselves repeating a lot of boilerplate code for flow initialisation, transaction composition, verifying, signing, counter-signing and finalising. The APIs in this release move much of that boilerplate code into helpful extension functions, making your flows much more succinct and easier to read.

🔵 **INFORMATION**

This API updates the underlying Corda dependencies from 4.6 to 4.8 (LTS).

---

### InitializeFlowStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a flow is being initialized.

```kotlin
object InitializeFlowStep : Step
```

---

### BuildTransactionStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a transaction is being built.

```kotlin
object BuildTransactionStep : Step
```

### Remarks

This progress tracker step is required when calling the `buildTransaction` extension function.

---

### VerifyTransactionStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a transaction is being verified.

```kotlin
object VerifyTransactionStep : Step
```

### Remarks

This progress tracker step is required when calling the `verifyTransaction` extension function.

---

### SignTransactionStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a transaction is being verified.

```kotlin
object SignTransactionStep : Step
```

### Remarks

This progress tracker step is required when calling the `signTransaction` or `collectSignaturesHandler` extension functions.

---

### CollectTransactionSignatureStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a transaction is being counter-signed.

```kotlin
object CollectTransactionSignaturesStep : Step
```

### Remarks

This progress tracker step is required when calling the `collectSignatures` extension function.

---

### SendStatesToRecordStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that that `StatesToRecord` is being send to a counter-party.

```kotlin
object SendStatesToRecordStep : Step
```

### Remarks

This progress tracker step is required when calling the `finalizeTransaction` extension function.

---

### ReceiveStatesToRecordStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that that `StatesToRecord` is being received from a counter-party.

```kotlin
object ReceiveStatesToRecordStep : Step
```

### Remarks

This progress tracker step is required when calling the `finalizeTransactionHandler` extension function.

---

### FinalizeTransactionStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a transaction is being finalized and recorded.

```kotlin
object FinalizeTransactionStep : Step
```

### Remarks

This progress tracker step is required when calling the `finalizeTransaction` extension function.

---

### RecordFinalizedTransactionStep _Object_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a progress tracker step indicating that a transaction is being recorded.

```kotlin
object RecordFinalizedTransactionStep : Step
```

### Remarks

This progress tracker step is required when calling the `finalizeTransactionHandler` extension function.

---

### buildTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Provides a DSL-like transaction builder.

```kotlin
@Suspendable
fun FlowLogic<*>.buildTransaction(
  notary: Party, 
  action: TransactionBuilder.() -> Unit
): TransactionBuilder
```

#### Remarks

To use this function, `BuildTransactionStep` will need to be evident in your progress tracker.

---

### verifyTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Verifies a transaction.

```kotlin
@Suspendable
fun FlowLogic<*>.verifyTransaction(transaction: TransactionBuilder)
```

#### Remarks

To use this function, `VerifyTransactionStep` will need to be evident in your progress tracker.

---

### signTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Signs a transaction.

```kotlin
@Suspendable
fun FlowLogic<*>.signTransaction(transaction: TransactionBuilder): SignedTransaction
```

#### Remarks

To use this function, `SignTransactionStep` will need to be evident in your progress tracker.

---

### collectSignatures _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Collects all remaining required signatures from the specified counter-parties.

```kotlin
@Suspendable
fun FlowLogic<*>.collectSignatures(
    transaction: SignedTransaction,
    sessions: Iterable<FlowSession>,
    additionalSigningAction: ((SignedTransaction) -> SignedTransaction)? = null
): SignedTransaction
```

#### Remarks

To use this function, `CollectTransactionSignaturesStep` will need to be evident in your progress tracker.

Due to the way this function works, it is intended to be paired with `collectSignaturesHandler` in the counter-flow. This function will filter out all required signing sessions from the sessions provided, and will then notify all sessions whether they are required to sign or not. For those sessions required to sign, it will collect their signature.

---

### collectSignaturesHandler _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Signs a transaction.

```kotlin
@Suspendable
fun FlowLogic<*>.collectSignaturesHandler(
    session: FlowSession,
    action: (SignedTransaction) -> Unit = {}
): SignedTransaction?
```

#### Remarks

To use this function, `SignTransactionStep` will need to be evident in your progress tracker.

Due to the way this function works, it is intended to be paired with `collectSignatures` in the initiating flow.

---

### finalizeTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Finalizes a transaction.

```kotlin
@Suspendable
fun FlowLogic<*>.finalizeTransaction(
    transaction: SignedTransaction,
    statesToRecordBySession: StatesToRecordBySession,
    ourStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT
): SignedTransaction
```

#### Remarks

To use this function, `SendStatesToRecordStep` and `FinalizeTransactionStep` will need to be evident in your progress tracker.

This function allows the initiator to specify how counter-parties should record states of the finalized transaction. For each session of the `statesToRecordBySession` object, the counter-party will receive their `StatesToRecord` enumeration, unless they have specified an override via the `finalizeTransactionHandler` function. Finally they will record the finalized transaction.

---

### finalizeTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Finalizes a transaction.

```kotlin
@Suspendable
fun FlowLogic<*>.finalizeTransaction(
    transaction: SignedTransaction,
    sessions: Iterable<FlowSession>,
    counterpartyStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT,
    ourStatesToRecord: StatesToRecord = StatesToRecord.ONLY_RELEVANT
): SignedTransaction
```

#### Remarks

To use this function, `SendStatesToRecordStep` and `FinalizeTransactionStep` will need to be evident in your progress tracker.

This function allows the initiator to specify how counter-parties should record states of the finalized transaction. Each session will record the transaction states according to the `counterpartyStatesToRecord` parameter, unless they have specified an override via the `finalizeTransactionHandler` function. Finally they will record the finalized transaction.

---

### finalizeTransactionHandler _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Finalizes a transaction.

```kotlin
@Suspendable
fun FlowLogic<*>.finalizeTransactionHandler(
    session: FlowSession,
    expectedTransactionId: SecureHash? = null,
    statesToRecord: StatesToRecord? = null
): SignedTransaction
```

#### Remarks

To use this function, `ReceiveStatesToRecordStep` and `RecordFinalizedTransactionStep` will need to be evident in your progress tracker.

This function will first receive the `StatesToRecord` from the initiating node, however this can be overridden using the `statesToRecord` parameter. Finally the transaction will be received and recorded.

---

### ourKeys _Extension Property_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Gets our signing keys from the key management service.

```kotlin
val KeyManagementService.ourKeys: List<PublicKey>
```

---

### requiredSigningKeys _Extension Property_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Gets all of the required signing keys from the transaction builder.

```kotlin
val TransactionBuilder.requiredSigningKeys: List<PublicKey>
```

---

### getOurSigningKeys _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Gets a list of our required signing keys for this transaction.

```kotlin
@Suspendable
fun TransactionBuilder.getOurSigningKeys(keyManagementService: KeyManagementService): List<PublicKey>
```

---

### getCounterpartySigningKeys _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Gets a list of required counter-party signing keys for this transaction.

```kotlin
fun TransactionBuilder.getCounterpartySigningKeys(keyManagementService: KeyManagementService): List<PublicKey>
```

---

### getTransactionParticipants _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Gets all of the transaction participants for this transaction.

```kotlin
fun TransactionBuilder.getTransactionParticipants(serviceHub: ServiceHub): List<AbstractParty>
```

---

### InternalSerializationWhitelist _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents an internal list of classes to be whitelisted for serialization.

```kotlin
internal class InternalSerializationWhitelist : SerializationWhitelist
```

#### Remarks

This adds `StatesToRecord` as instances of this enumeration are send to counter-parties during transaction finalization.

---

### StatesToRecordBySession _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a mapping of `FlowSession` to `StatesToRecord`, allowing a transaction initiator to specify how each counter-party should record the states of a transaction.

```kotlin
class StatesToRecordBySession(statesToRecordBySession: Map<FlowSession, StatesToRecord> = emptyMap())
```

---

