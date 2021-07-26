![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log - Version 2.1.2

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

This release contains a bug fix in the contract API to allow `contractStateType` to be overridden in resolvable classes, where the generic type parameter of the derived resolvable is `T`.

---

### AbstractPluralResolvable _Class_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Represents the base class for implementing plural (one-to-many) contract state resolvers.

```kotlin
abstract class AbstractPluralResolvable<T> : PluralResolvable<T> where T : ContractState
```

#### Remarks

In version 2.0.0, the `contractStateType` of this class was final and automatically resolved the class of the generic parameter type in a derived class, however this breaks if the generic parameter type in the derived class is also generic.

In this version the property is declared using the `open` modifier, allowing derived classes to specify the `contractStateType` manually.

---

### AbstractSingularResolvable _Class_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Represents the base class for implementing singular (one-to-one) contract state resolvers.

```kotlin
abstract class AbstractSingularResolvable<T> : SingularResolvable<T> where T : ContractState
```

#### Remarks

In version 2.0.0, the `contractStateType` of this class was final and automatically resolved the class of the generic parameter type in a derived class, however this breaks if the generic parameter type in the derived class is also generic.

In this version the property is declared using the `open` modifier, allowing derived classes to specify the `contractStateType` manually.

---

### 
