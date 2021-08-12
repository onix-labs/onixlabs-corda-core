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

### 
