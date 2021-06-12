![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log - Version 1.2.0

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

This release contains more APIs for contract design, specifically for verifiable and signed commands.

---

### ContractID *Interface*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Defines an interface which automatically binds a contract ID to a contract class.

```kotlin
interface ContractID
```

#### Remarks

Rather than referencing the contract by a string or by its canonical name, implementing `ContractID` on a companion object adds the contract ID to the class for you automatically.

---

### SignatureData *Class*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Represents an array of unsigned bytes, and its signed equivalent.

```kotlin
@CordaSerializable
data class SignatureData(
    private val content: ByteArray, 
    private val signature: DigitalSignature
)
```

---

### SignedCommandData *Interface*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Defines a contract command that must include a signature.

```kotlin
interface SignedCommandData : CommandData
```

#### Remarks

This can be used within a contract to check that a particular contract participant signed over the command; usually the transaction initiator.

---

### VerifiedCommandData *Interface*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Defines a contract command that can verify a ledger transaction.

```kotlin
interface VerifiedCommandData : CommandData
```

#### Remarks

Rather than commands simply being marker objects to determine which verification to execute, this leans towards the single responsibility principle, whereby each command is responsible for its verification.

---

### cast *Extension Function*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Casts a `StateAndRef` of an unknown `ContractState` to a `StateAndRef` of type `T`.

```kotlin
fun <T> StateAndRef<*>.cast(contractStateClass: Class<T>): StateAndRef<T> where T : ContractState

inline fun <reified T> StateAndRef<*>.cast(): StateAndRef<T> where T : ContractState
```

>   🔵 **INFORMATION**
>
>   This API exists in version 1.0.0 but only using reified generics.

---

### cast *Extension Function*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Casts an iterable of `StateAndRef` of an unknown `ContractState` to a list of `StateAndRef` of type `T`.

```kotlin
fun <T> Iterable<StateAndRef<*>>.cast(contractStateClass: Class<T>): List<StateAndRef<T>> where T : ContractState

inline fun <reified T> Iterable<StateAndRef<*>>.cast(): List<StateAndRef<T>> where T : ContractState
```

>   🔵 **INFORMATION**
>
>   This API exists in version 1.1.0 but only using reified generics.

---

