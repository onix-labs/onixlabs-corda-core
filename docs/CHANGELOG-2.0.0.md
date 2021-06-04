![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log - Version 2.0.0

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

#### 🔴 WARNING

**This release contains breaking changes from version 1.2.0 and is not backwards compatible.**

-   `FindStateFlow<T>` and `FindStatesFlow<T>` have been removed in favour of using the new vault query service and query DSL.
-   `Resolvable<T>` has been refactored to allow state resolution of sungular (one-to-one) and plural (one-to-many) contract states (see below).

#### 🔵 INFORMATION

As of version 1.0.0, packaged releases of this API has been signed with the ONIXLabs production signing key. Historically, clones of this repository would have failed to build, since the ONIXLabs production signing key is a secret. Version 2.0.0 ships with the ONIXLabs developer key, allowing this repository to be cloned, built and tested locally.

As of version 2.0.0 this project contains test CorDapp modules in order to thoroughly test and exercise the API surface, as opposed to limited tests per module.

---

### getArgumentsTypes _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core

Gets the argument types from a generic superclass.

```kotlin
fun Class<*>.getArgumentTypes(): List<Type>
```

---

### getArgumentType _Extension Function_

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
>   This API exists in version 1.2.0 however in version 2.0.0 the `content` and `signature` properties are public, where they were originally private.

---

### allowCommands _Extension Function_

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

Provides utility for `VerifiedCommandData` implementations, specifying which commands are allowed within a contract. This function will verify allowed commands, or throw an `IllegalArgumentException` exception if the command is not allowed.

```kotlin
fun <T : VerifiedCommandData> LedgerTransaction.allowCommands(commandClass: Class<T>, vararg allowed: Class<out T>)

inline fun <reified T : VerifiedCommandData> LedgerTransaction.allowCommands(vararg allowed: Class<out T>
```

---

### QueryDsl _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

Represents a DSL for building vault queries.

```kotlin
class QueryDsl<T : ContractState> internal constructor(
    private var queryCriteria: QueryCriteria,
    private var page: PageSpecification,
    private var sort: Sort
)
```

---

### QueryDslContext _Annotation Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Specifies that a function is contextually party of the query DSL.

```kotlin
@DslMarker
annotation class QueryDslMarker
```

---

### VaultAdapter _Abstract Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Represents the base class for implementing vault adapters. This uses the adapter design pattern to unify the vault query API between `ServiceHub` and `CordaRPCOps` implementations.

```kotlin
internal abstract class VaultAdapter<T : ContractState>(
  protected val contractStateType: Class<T>
)
```

---

### VaultAdapterCordaRPCOps _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Represents a vault service adapter implementation for `CordaRPCOps`.

```kotlin
internal class VaultAdapterCordaRPCOps<T : ContractState>(
    private val cordaRPCOps: CordaRPCOps,
    contractStateType: Class<T>
) : VaultAdapter<T>(contractStateType)
```

---

### VaultServiceAdapterServiceHub _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Represents a vault service adapter implementation for `ServiceHub`.

```kotlin
internal class VaultAdapterServiceHub<T : ContractState>(
    private val serviceHub: ServiceHub,
    contractStateType: Class<T>
) : VaultAdapter<T>(contractStateType)
```

---

### VaultObservable _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Represents an observable vault state.

```kotlin
@CordaSerializable
class VaultObservable<T : ContractState>(
    val state: TransactionState<T>,
    val ref: StateRef,
    val status: Vault.StateStatus,
    val flowId: UUID?
)
```

---

### VaultSequence _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Represents a lazily evaluated sequence of vault query results.

```kotlin
class VaultSequence<T : ContractState> internal constructor(
    private val service: VaultService<T>,
    private val criteria: QueryCriteria,
    private val paging: PageSpecification,
    private val sorting: Sort
) : Sequence<StateAndRef<T>>
```

---

### VaultService _Class_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Represents a service for managing vault querying and tracking.

```kotlin
class VaultService<T : ContractState> private constructor(
    private val adapter: VaultAdapter<T>,
    internal val contractStateType: Class<T>
)
```

---

### vaultServiceFor _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Creates a vault service, bound to the specified `ContractState` type. This allows vault services to be created from either `CordaRPCOps` or `ServiceHub` instances.

```kotlin
inline fun <reified T : ContractState> CordaRPCOps.vaultServiceFor(): VaultService<T>

inline fun <reified T : ContractState> ServiceHub.vaultServiceFor(): VaultService<T>
```

---

### vaultQuery _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Creates a `QueryCriteria` using the Query DSL.

```kotlin
fun <T : ContractState> vaultQuery(
    contractStateType: Class<T>,
    stateStatus: Vault.StateStatus,
    relevancyStatus: Vault.RelevancyStatus,
    page: PageSpecification,
    sort: Sort,
    action: QueryDsl<T>.() -> Unit
): QueryCriteria

inline fun <reified T : ContractState> vaultQuery(
    stateStatus: Vault.StateStatus,
    relevancyStatus: Vault.RelevancyStatus,
    page: PageSpecification,
    sort: Sort,
    noinline action: QueryDsl<T>.() -> Unit
): QueryCriteria
```

---

### any _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Determines whether the sequence contains any elements.

```kotlin
fun <T : ContractState> VaultService<T>.any(
  criteria: QueryCriteria
): Boolean

fun <T : ContractState> VaultService<T>.any(
  action: QueryDsl<T>.() -> Unit
): Boolean
```

---

### count _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Counts the number of elements in a sequence.

```kotlin
fun <T : ContractState> VaultService<T>.count(
  criteria: QueryCriteria
): Int

fun <T : ContractState> VaultService<T>.count(
  action: QueryDsl<T>.() -> Unit
): Int
```

---

### filter _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Filters a sequence of elements based on the specified query criteria.

```kotlin
fun <T : ContractState> VaultService<T>.filter(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): VaultSequence<T>

fun <T : ContractState> VaultService<T>.filter(
  action: QueryDsl<T>.() -> Unit
): VaultSequence<T> 
```

---

### first _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Obtains the first element of a sequence.

```kotlin
fun <T : ContractState> VaultService<T>.first(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): StateAndRef<T>

fun <T : ContractState> VaultService<T>.first(
  action: QueryDsl<T>.() -> Unit
): StateAndRef<T>
```

---

### firstOrNull _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Obtains the first element of a sequence, or null if no element is found.

```kotlin
fun <T : ContractState> VaultService<T>.firstOrNull(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): StateAndRef<T>

fun <T : ContractState> VaultService<T>.firstOrNull(
  action: QueryDsl<T>.() -> Unit
): StateAndRef<T>
```

---

### last _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Obtains the last element of a sequence.

```kotlin
fun <T : ContractState> VaultService<T>.last(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): StateAndRef<T>

fun <T : ContractState> VaultService<T>.last(
  action: QueryDsl<T>.() -> Unit
): StateAndRef<T>
```

---

### lastOrNull _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Obtains the last element of a sequence, or null if no element is found.

```kotlin
fun <T : ContractState> VaultService<T>.lastOrNull(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): StateAndRef<T>

fun <T : ContractState> VaultService<T>.lastOrNull(
  action: QueryDsl<T>.() -> Unit
): StateAndRef<T>
```

---

### single _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Obtains a single, specific element of a sequence.

```kotlin
fun <T : ContractState> VaultService<T>.single(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): StateAndRef<T>

fun <T : ContractState> VaultService<T>.single(
  action: QueryDsl<T>.() -> Unit
): StateAndRef<T>
```

---

### singleOrNull _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Obtains a single, specific element of a sequence, or null if no element is found.

```kotlin
fun <T : ContractState> VaultService<T>.singleOrNull(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): StateAndRef<T>

fun <T : ContractState> VaultService<T>.singleOrNull(
  action: QueryDsl<T>.() -> Unit
): StateAndRef<T>
```

---

### toList _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Obtains a sequence of elements as a `List`.

```kotlin
fun <T : ContractState> VaultService<T>.toList(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): List<StateAndRef<T>>
```

---

### toSet _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Obtains a sequence of elements as a `Set`.

```kotlin
fun <T : ContractState> VaultService<T>.toSet(
    criteria: QueryCriteria,
    paging: PageSpecification,
    sorting: Sort
): List<StateAndRef<T>>
```

---

### subscribe _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Subscribes to vault tracking updates.

```kotlin
fun <T : ContractState> VaultService<T>.subscribe(
    criteria: QueryCriteria = defaultTrackingCriteria,
    paging: PageSpecification = MAXIMUM_PAGE_SPECIFICATION,
    sorting: Sort = DEFAULT_SORTING,
    observer: (VaultObservable<T>) -> Unit
)
```

---

### isNull _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with null value.

```kotlin
fun <T : StatePersistable, P> KProperty1<T, P?>.isNull(): QueryCriteria
```

---

### isNotNull _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a non-null value.

```kotlin
fun <T : StatePersistable, P> KProperty1<T, P?>.isNotNull(): QueryCriteria
```

---

### equalTo _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value equal to the specified value.

```kotlin
infix fun <T : StatePersistable, P> KProperty1<T, P?>.equalTo(value: P): QueryCriteria
```

---

### notEqualTo _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value not equal to the specified value.

```kotlin
infix fun <T : StatePersistable, P> KProperty1<T, P?>.notEqualTo(value: P): QueryCriteria
```

---

### greaterThan _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value greater than to the specified value.

```kotlin
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.greaterThan(value: P): QueryCriteria
```

---

### greaterThanOrEqualTo _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value greater than or equal to to the specified value.

```kotlin
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.greaterThanOrEqualTo(value: P): QueryCriteria
```

---

### lessThan _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value less than to the specified value.

```kotlin
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.lessThan(value: P): QueryCriteria
```

---

### lessThanOrEqualTo _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value less than or equal to to the specified value.

```kotlin
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.lessThanOrEqualTo(value: P): QueryCriteria
```

---

### between _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value between the specified minimum and maximum values.

```kotlin
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.between(range: ClosedRange<P>): QueryCriteria
```

---

### like _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value like the specified value.

```kotlin
infix fun <T : StatePersistable> KProperty1<T, String?>.like(value: String): QueryCriteria
```

---

### notLike _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value not like the specified value.

```kotlin
infix fun <T : StatePersistable> KProperty1<T, String?>.notLike(value: String): QueryCriteria
```

---

### within _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value within the specified collection.

```kotlin
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.within(value: Collection<P>): QueryCriteria
```

---

### notWithin _Extension Function_

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

Builds a query criterion for a property with a value not within the specified collection.

```kotlin
infix fun <T : StatePersistable, P : Comparable<P>> KProperty1<T, P?>.notWithin(value: Collection<P>): QueryCriteria
```

---

### DEFAULT_SORTING *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

#### Description

The default sorting order.

```kotlin
val DEFAULT_SORTING: Sort
```

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.services`.

---

### DEFAULT_PAGE_SPECIFICATION *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

#### Description

The default page specification.

```kotlin
val DEFAULT_PAGE_SPECIFICATION: PageSpecification
```

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.services`.

---

### MAXIMUM_PAGE_SPECIFICATION *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

#### Description

The maximum page specification.

```kotlin
val MAXIMUM_PAGE_SPECIFICATION: PageSpecification
```

>   🔴 **WARNING**
>
>   **This API is not backwards compatible!**
>
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.services`.

---

### andWithExpressions *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

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
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.services`.

---

### orWithExpressions *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.services

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
>   This API exists in version 1.0.0 however in version 2.0.0 it has moved from `io.onixlabs.corda.core.workflow` to `io.onixlabs.corda.core.services`.

---

