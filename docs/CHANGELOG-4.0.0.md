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

