![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log - Version 2.0.0

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

#### 🔴 WARNING

**This release contains breaking changes from version 1.2.0 and is not backwards compatible.**

This release contains several new APIs and some API changes; including:

-   A set of APIs for obtaining underlying generic type information, used within this API and beyond.
-   A model for creating one-to-one and one-to-many state relationships.
-   A query parameter model for improved vault query capability.
-   A reference CorDapp which implements, demonstrates and tests the features of this API.

#### 🔵 INFORMATION

As of version 1.0.0, packaged releases of this API has been signed with the ONIXLabs production signing key. Historically, clones of this repository would have failed to build, since the ONIXLabs production signing key is a secret. Version 2.0.0 ships with the ONIXLabs developer key, allowing this repository to be cloned, built and tested locally.

---

### TypeInfo _Class_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core

Represents a graph of type information about a generic type.

```kotlin
class TypeInfo<T> private constructor(val typeClass: Class<T>, val typeArguments: List<TypeInfo<*>>)
```

---

### TypeReference _Abstract Class_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core

Represents a type reference which obtains full generic type information for the underlying generic type.

```kotlin
abstract class TypeReference<T> : Comparable<TypeReference<T>>
```

>   🔵  **INFORMATION**
>
>   This implementation is inspired by the `TypeReference<T>` class in [fasterxml, jackson-core](https://fasterxml.github.io/jackson-core/javadoc/2.8/com/fasterxml/jackson/core/type/TypeReference.html).

---

### typeReference _Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core

Creates a type reference of the reified generic type.

```kotlin
inline fun <reified T> typeReference(): TypeReference<T>
```

---

### getTypeArguments _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core

Gets the argument types from a generic superclass.

```kotlin
fun Class<*>.getArgumentTypes(): List<Type>
```

---

### getTypeArgument _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core

Gets an argument type from a generic superclass.

```kotlin
fun Class<*>.getArgumentType(index: Int): Type
```

---

### toClass _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core

Converts a type to a class.

```kotlin
fun Type.toClass(): Class<*>
```

---

### toTypedClass _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core

Converts a type to a class.

```kotlin
fun <T> Type.toTypedClass(): Class<T>
```

---

### AbstractPluralResolvable _Class_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Represents the base class for implementing plural (one-to-many) contract state resolvers.

```kotlin
abstract class AbstractPluralResolvable<T> : PluralResolvable<T> where T : ContractState
```

#### Remarks

Corda states are persisted to a relational database, however due to the nature of the ledger and the way states are created, evolved and spent, it's hard to model relational data with states. This brings Corda one step closer, allowing Corda states to model one-to-many relationships with other Corda states. To model a one-to-one state relationship, see `SingularResolvable<T>` and `AbstractSingularResolvable<T>`.

---

### AbstractSingularResolvable _Class_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Represents the base class for implementing singular (one-to-one) contract state resolvers.

```kotlin
abstract class AbstractSingularResolvable<T> : SingularResolvable<T> where T : ContractState
```

#### Remarks

Corda states are persisted to a relational database, however due to the nature of the ledger and the way states are created, evolved and spent, it's hard to model relational data with states. This brings Corda one step closer, allowing Corda states to model one-to-one relationships with other Corda states. To model a one-to-many state relationship, see `PluralResolvable<T>` and `AbstractPluralResolvable<T>`.

---

### PluralResolvable _Interface_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Defines an object which resolves a collection of `ContractState`.

```kotlin
interface PluralResolvable<T> where T : ContractState
```

#### Remarks

An abstract implementation of this interface can be found in `AbstractPluralResolvable<T>`.

---

### SingularResolvable _Interface_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Defines an object which resolves a `ContractState`.

```kotlin
interface SingularResolvable<T> where T : ContractState
```

#### Remarks

An abstract implementation of this interface can be found in `AbstractSingularResolvable<T>`.

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 and was originally called `Resolvable<T>` however in version 2.0.0 the model has been extended to support one-to-one, and one-to-many relationships.

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
>   This API exists in version 1.2.0 however in version 2.0.0 the `content` and `signature` properties are public, there they were originally private.

---

### allowCommands _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Provides utility for `VerifiedCommandData` implementations, specifying which commands are allowed within a contract. This function will verify allowed commands, or throw an `IllegalArgumentException` exception if the command is not allowed.

```kotlin
fun <T : VerifiedCommandData> LedgerTransaction.allowCommands(commandClass: Class<T>, vararg allowed: Class<out T>)

inline fun <reified T : VerifiedCommandData> LedgerTransaction.allowCommands(vararg allowed: Class<out T>)
```

---

### QueryEquatable _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Represents the base class for implementing equatable query parameters.

```kotlin
@CordaSerializable
sealed class QueryEquatable<T>
```

#### Remarks

Internal implementations of this class are exposed through companion and top-level functions.

---

### QueryComparable _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Represents the base class for implementing comparable query parameters.

```kotlin
@CordaSerializable
sealed class QueryComparable<T>
```

#### Remarks

Internal implementations of this class are exposed through companion and top-level functions.

---

### QueryString _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Represents the base class for implementing string query parameters.

```kotlin
@CordaSerializable
sealed class QueryString<T>
```

#### Remarks

Internal implementations of this class are exposed through companion and top-level functions.

---

### equatableEqualTo _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates an "equal to" equatable query.

```kotlin
fun <T> equatableEqualTo(value: T, ignoreCase: Boolean = false): QueryEquatable<T>
```

---

### equatableNotEqualTo _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "not equal to" equatable query.

```kotlin
fun <T> equatableNotEqualTo(value: T, ignoreCase: Boolean = false): QueryEquatable<T>
```

---

### comparableEqualTo _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates an "equal to" comparable query.

```kotlin
fun <T> comparableEqualTo(value: T, ignoreCase: Boolean = false): QueryComparable<T>
```

---

### comparableNotEqualTo _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "not equal to" comparable query.

```kotlin
fun <T> comparableNotEqualTo(value: T, ignoreCase: Boolean = false): QueryComparable<T>
```

---

### greaterThan _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "greater than" comparable query.

```kotlin
fun <T> greaterThan(value: T): QueryComparable<T>
```

---

### greaterThanOrEqualTo _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "greater than or equal to" comparable query.

```kotlin
fun <T> greaterThanOrEqualTo(value: T): QueryComparable<T>
```

---

### lessThan _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "less than" comparable query.

```kotlin
fun <T> lessThan(value: T): QueryComparable<T>
```

---

### lessThanOrEqualTo _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "less than or equal to" comparable query.

```kotlin
fun <T> lessThanOrEqualTo(value: T): QueryComparable<T>
```

---

### between _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "between" comparable query.

```kotlin
fun <T : Comparable<T>> between(range: ClosedRange<T>): QueryComparable<T>
```

---

### within _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "within" (or "in") comparable query.

```kotlin
fun <T : Comparable<T>> within(items: Iterable<T>, ignoreCase: Boolean = false): QueryComparable<T>
```

---

### notWithin _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "not within" (or "not in") comparable query.

```kotlin
fun <T : Comparable<T>> notWithin(items: Iterable<T>, ignoreCase: Boolean = false): QueryComparable<T>
```

---

### stringEqualTo _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates an "equal to" string query.

```kotlin
fun <T> stringEqualTo(value: T, ignoreCase: Boolean = false): QueryString<T>
```

---

### stringNotEqualTo _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "not equal to" string query.

```kotlin
fun <T> stringNotEqualTo(value: T, ignoreCase: Boolean = false): QueryString<T>
```

---

### like _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "like" string query.

```kotlin
fun <T> like(value: T, ignoreCase: Boolean = false): QueryString<T>
```

---

### notLike _Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

Creates a "not like" string query.

```kotlin
fun <T> notLike(value: T, ignoreCase: Boolean = false): QueryString<T>
```

---

### FindStateFlow *Abstract Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

#### Description

Represents the base class for implementing a query that finds a single state.

```kotlin
abstract class FindStateFlow<T> : FlowLogic<StateAndRef<T>?>() where T : ContractState
```

#### Remarks

This design pattern enables developers to be more consistent in their approach to performing vault queries, regardless of whether they're being performed from within the node (using `ServiceHub`) or from an RPC client (using `CordaRPCOps`).

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.query`.

---

### FindStatesFlow *Abstract Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

#### Description

Represents the base class for implementing a query that finds multiple states.

```kotlin
abstract class FindStatesFlow<T> : FlowLogic<List<StateAndRef<T>>>() where T : ContractState
```

#### Remarks

This design pattern enables developers to be more consistent in their approach to performing vault queries, regardless of whether they're being performed from within the node (using `ServiceHub`) or from an RPC client (using `CordaRPCOps`).

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.query`.

---

### DEFAULT_SORTING *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

#### Description

The default sorting order.

```kotlin
val DEFAULT_SORTING: Sort
```

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.query`.

---

### DEFAULT_PAGE_SPECIFICATION *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

#### Description

The default page specification.

```kotlin
val DEFAULT_PAGE_SPECIFICATION: PageSpecification
```

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.query`.

---

### MAXIMUM_PAGE_SPECIFICATION *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

#### Description

The maximum page specification.

```kotlin
val MAXIMUM_PAGE_SPECIFICATION: PageSpecification
```

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.query`.

---

### andWithExpressions *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

#### Description

Builds a custom query criteria. This combines all non-null query expressions using logical AND.

```kotlin
fun <T : StatePersistable> QueryCriteria.VaultQueryCriteria.andWithExpressions(
    vararg expressions: CriteriaExpression<T, Boolean>?
): QueryCriteria
```

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.query`.

---

### orWithExpressions *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.query

#### Description

Builds a custom query criteria. This combines all non-null query expressions using logical OR.

```kotlin
fun <T : StatePersistable> QueryCriteria.VaultQueryCriteria.andWithExpressions(
    vararg expressions: CriteriaExpression<T, Boolean>?
): QueryCriteria
```

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.query`.

---

