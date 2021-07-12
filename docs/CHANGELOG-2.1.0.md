![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log - Version 0.0.0

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

This release contains several API improvements for workflow design and composability. Typically when writing flows, we find ourselves repeating a lot of boilerplate code for flow initialisation, transaction composition, verifying, signing, counter-signing and finalising. The APIs in this release move much of that boilerplate code into helpful extension functions, making your flows much more succinct and easier to read.

---

### buildTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Provides a DSL-like transaction builder.

```kotlin
fun FlowLogic<*>.buildTransaction(notary: Party, action: TransactionBuilder.() -> Unit): TransactionBuilder
```

#### Remarks

To use this function, `BuildingTransactionStep` will need to be evident in your progress tracker.

---

### verifyTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Verifies a transaction.

```kotlin
fun FlowLogic<*>.verifyTransaction(transaction: TransactionBuilder)
```

#### Remarks

To use this function, `VerifyingTransactionStep` will need to be evident in your progress tracker.

---

### signTransaction _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Signs a transaction.

```kotlin
fun FlowLogic<*>.signTransaction(transaction: TransactionBuilder): SignedTransaction
```

#### Remarks

To use this function, `SigningTransactionStep` will need to be evident in your progress tracker.

---

### collectSignatures _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Collects all remaining required signatures from the specified counter-parties.

```kotlin
fun FlowLogic<*>.collectSignatures(transaction: SignedTransaction, sessions: Iter)
```

#### Remarks

This extension property can be used to determine whether the participants of a state are unique, regardless of the ordering of those participants.

---





















### TypeName _Declaration_

**Module:** onixlabs-corda-core-modulename

**Package:** io.onixlabs.corda.core.packagename

Description of the type or feature.

```kotlin
class TypeName
```

#### Remarks

This extension property can be used to determine whether the participants of a state are unique, regardless of the ordering of those participants.

---

