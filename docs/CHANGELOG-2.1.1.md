![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log - Version 2.1.1

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

This release contains a bug fix in the workflow API to allow additional signing steps to remain suspendible when called from the `collectSignatures` function.

---

### collectSignatures _Extension Function_

**Module:** onixlabs-corda-core-modulename

**Package:** io.onixlabs.corda.core.packagename

Collects all remaining required signatures from the specified counter-parties.

```kotlin
@Suspendable
inline fun FlowLogic<*>.collectSignatures(
    transaction: SignedTransaction,
    sessions: Iterable<FlowSession>,
    additionalSigningAction: ((SignedTransaction) -> SignedTransaction) = { it }
): SignedTransaction
```

#### Remarks

In version 2.1.0, adding additional signing requirements caused an exception where the transaction context was missing. This is typical when a function is not marked `@Suspendable`. Since `additionalSigningAction` is a functional parameter it cannot be marked suspendable, therefore the solution is to inline the outer `collectSignatures` function signature.

---

