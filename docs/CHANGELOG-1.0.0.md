![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onixlabs-website/main/src/assets/images/logo/full/original/original-md.png)

# Change Log - Version 1.0.0

This document serves as the change log for the ONIXLabs Corda Core API.

## Release Notes

This is the initial release of the ONIXLabs Corda Core API and contains features for Corda contract, workflow and integration design.

---

### ChainState *Interface*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Defines a Corda chain state.

```kotlin
interface ChainState
```

#### Remarks

A chain state is similar to a `LinearState` in that it represents a set of state transitions. Chain states adopt a concept similar to a blockchain, where each new state transition references the previous one by its `StateRef`, or `null` if it's the first state in the chain.

---

### Hashable *Interface*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Defines an object that can produce a unique hash.

```kotlin
interface Hashable
```

#### Remarks

When used in conjunction with the `ChainState` interface, this can be useful for modelling states that always produce a unique hash for every state transition.

---

### Resolvable *Interface*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Defines an object which resolves a `ContractState`.

```kotlin
interface Resolvable<T> where T : ContractState
```

#### Remarks

This is the fundamental principle on which state pointers are implemented in Corda; their ability to maintain and resolve a reference back to another known state in the vault. The implementation here is flexible and open, allowing developers to model resolvable as a design pattern, rather than as a strict implementation detail.

---

### TransactionResolution *Enum Class*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Defines how to resolve states from a transaction, and is used by the `Resolvable` interface.

```kotlin
enum class TransactionResolution
```

#### Entries

| Name          | Description                                                  |
| ------------- | ------------------------------------------------------------ |
| **INPUT**     | Defines that a resolvable should resolve from a transaction input state. |
| **OUTPUT**    | Defines that a resolvable should resolve from a transaction output state. |
| **REFERENCE** | Defines that a resolvable should resolve from a transaction reference input state. |

---

### owningKeys *Extension Property*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Gets the owning keys from an `Iterable` of `AbstractParty`.

```kotlin
val Iterable<AbstractParty>.owningKeys: Set<PublicKey> 
```

---

### participantHash *Extension Property*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Gets a hash from an `Iterable` of `AbstractParty`.

```kotlin
val Iterable<AbstractParty>.participantHash: SecureHash
```

#### Remarks

This extension property can be used to determine whether the participants of a state are unique, regardless of the ordering of those participants.

---

### isPointingTo *Extension Function*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Determines whether the current state is pointing to the specified `StateRef`, or `StateAndRef`.

```kotlin
fun <T> §T.isPointingTo(stateAndRef: StateAndRef<T>): Boolean where T : ChainState
```

---

### cast *Extension Function*

**Module:** onixlabs-corda-core-contract

**Package:** io.onixlabs.corda.core.contract

#### Description

Casts a `StateAndRef` of an unknown `ContractState` to a `StateAndRef` of type `T`.

```kotlin
inline fun <reified T> StateAndRef<*>.cast(): StateAndRef<T> where T : ContractState
```

---

### FindStateFlow *Abstract Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents the base class for implementing a query that finds a single state.

```kotlin
abstract class FindStateFlow<T> : FlowLogic<StateAndRef<T>?>() where T : ContractState
```

#### Remarks

This design pattern enables developers to be more consistent in their approach to performing vault queries, regardless of whether they're being performed from within the node (using `ServiceHub`) or from an RPC client (using `CordaRPCOps`).

---

### FindStatesFlow *Abstract Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents the base class for implementing a query that finds multiple states.

```kotlin
abstract class FindStatesFlow<T> : FlowLogic<List<StateAndRef<T>>>() where T : ContractState
```

#### Remarks

This design pattern enables developers to be more consistent in their approach to performing vault queries, regardless of whether they're being performed from within the node (using `ServiceHub`) or from an RPC client (using `CordaRPCOps`).

---

### Message *Open Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents a transient message.

```kotlin
open class Message<T>(val data: T, val id: UUID = UUID.randomUUID()) where T : Any
```

---

### MessageAcknowledgement *Open Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents an acknowledgement to a transient message.

```kotlin
open class MessageAcknowledgement(val id: UUID)
```

---

### SendMessageFlow *Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents a sub-flow that sends a message to the specified counter-parties.

```kotlin
class SendMessageFlow<T>(
    private val message: T,
    private val sessions: Set<FlowSession>,
    private val requestAcknowledgement: Boolean = false,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Map<Party, MessageAcknowledgement>>() where T : Message<*>
```

#### Remarks

Use the built-in `SendMessageFlow.Initiator` class in order to use this class as an initiating flow.

```kotlin
@StartableByRPC
@StartableByService
@InitiatingFlow
class Initiator<T>(
    private val message: T,
    private val counterparties: Collection<Party>
) : FlowLogic<Map<Party, MessageAcknowledgement>>() where T : Message<*>
```

---

### ReceiveMessageFlow *Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents a sub-flow that receives a message from the specified counter-parties.

```kotlin
class ReceiveMessageFlow<T>(
    private val session: FlowSession,
    private val requestAcknowledgement: Boolean = false,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Pair<Party, T>>() where T : Message<*>
```

#### Remarks

Use the built-in `ReceiveMessageFlow.Receiver` class in order to register it as an initiated-by flow, and listen for message results.

```kotlin
@InitiatedBy(SendMessageFlow.Initiator::class)
class Receiver<T>(private val session: FlowSession) : FlowLogic<Pair<Party, T>>() where T : Message<*>
```

---

### SendMessageAcknowledgementFlow *Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents a sub-flow that sends a message acknowledgement to the specified counter-party.

```kotlin
class SendMessageAcknowledgementFlow<T>(
    private val acknowledgement: T,
    private val session: FlowSession,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Unit>() where T : MessageAcknowledgement
```

#### Remarks

Use the built-in `SendMessageAcknowledgementFlow.Initiator` class in order to use this class as an initiating flow.

```kotlin
@StartableByRPC
@StartableByService
@InitiatingFlow
class Initiator<T>(
    private val acknowledgement: T,
    private val counterparty: Party
) : FlowLogic<Unit>() where T : MessageAcknowledgement
```

---

### ReceiveMessageAcknowledgementFlow *Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents a sub-flow that receives a message acknowledgement from the specified counter-party.

```kotlin
class ReceiveMessageAcknowledgementFlow<T>(
    private val session: FlowSession,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Pair<Party, T>>()
```

#### Remarks

Use the built-in `ReceiveMessageAcknowledgementFlow.Receiver` class in order to register it as an initiated-by flow, and listen for message acknowledgement results.

```kotlin
@InitiatedBy(SendMessageAcknowledgementFlow.Initiator::class)
class Receiver<T>(private val session: FlowSession) : FlowLogic<Pair<Party, T>>() where T : MessageAcknowledgement
```

---

### TransactionNote *Data Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents a transaction note.

```kotlin
data class TransactionNote(val transactionId: SecureHash, val text: String)
```

---

### SendTransactionNoteFlow *Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents a sub-flow that sends a transaction note to the specified counter-parties.

```kotlin
class SendTransactionNoteFlow(
    private val transactionNote: TransactionNote,
    private val sessions: Collection<FlowSession>,
    private val addNoteToTransaction: Boolean = true,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Unit>()
```

#### Remarks

Use the built-in `SendTransactionNoteFlow.Initiator` class in order to use this class as an initiating flow.

```kotlin
@StartableByRPC
@StartableByService
@InitiatingFlow
class Initiator(
    private val transactionNote: TransactionNote,
    private val counterparties: Iterable<Party>,
    private val addNoteToTransaction: Boolean = true
) : FlowLogic<Unit>()
```

---

### ReceiveTransactionNoteFlow *Class*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Represents a sub-flow that receives a transaction note from the specified counter-party.

```kotlin
class ReceiveTransactionNoteFlow(
    private val session: FlowSession,
    private val persist: Boolean = true,
    private val expectedTransactionId: SecureHash? = null,
    override val progressTracker: ProgressTracker = tracker()
) : FlowLogic<Pair<Party, TransactionNote>>()
```

#### Remarks

Use the built-in `ReceiveTransactionNoteFlow.Receiver` class in order to register it as an initiated-by flow, and listen for transaction note results.

```kotlin
@InitiatedBy(SendTransactionNoteFlow.Initiator::class)
class Receiver(private val session: FlowSession) : FlowLogic<Pair<Party, TransactionNote>>()
```

---

### firstNotary *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Gets the first available notary.

```kotlin
val FlowLogic<*>.firstNotary: Party
```

#### Remarks

This extension property will throw a `NoSuchElementException` if there are no available notaries.

---

### randomNotary *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Gets a randomly available notary.

```kotlin
val FlowLogic<*>.randomNotary: Party
```

#### Remarks

This extension property will throw a `NoSuchElementException` if there are no available notaries.

---

### getPreferredNotary *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Gets the preferred notary from the CorDapp config, or alternatively a default notary in the event that a preferred notary has not been specified in the CorDapp config.

```kotlin
@Suspendable
fun FlowLogic<*>.getPreferredNotary(
    serviceHub: ServiceHub = this.serviceHub,
    defaultSelector: (ServiceHub) -> Party = { firstNotary }
): Party
```

#### Remarks

This extension property will throw an `IllegalArgumentException` if the preferred notary cannot be found in the network map cache.

---

### currentStep *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Sets the current progress tracker step.

```kotlin
@Suspendable
fun FlowLogic<*>.currentStep(step: Step, log: Boolean = true, additionalLogInfo: String? = null)
```

---

### initiateFlows *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Initiates flow sessions for the specified parties and/or states, except for identities that belong to the local node, since flow sessions are not required locally.

```kotlin
@Suspendable
fun FlowLogic<*>.initiateFlows(vararg parties: AbstractParty): Set<FlowSession>

@Suspendable
fun FlowLogic<*>.initiateFlows(vararg states: ContractState): Set<FlowSession>

@Suspendable
fun FlowLogic<*>.initiateFlows(parties: Iterable<AbstractParty>, vararg states: ContractState): Set<FlowSession>
```

---

### checkSufficientSessions *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Checks that sufficient flow sessions have been provided for the specified states, of from the input and output states of a transaction builder.

```kotlin
@Suspendable
fun FlowLogic<*>.checkSufficientSessions(sessions: Iterable<FlowSession>, states: Iterable<ContractState>)

@Suspendable
fun FlowLogic<*>.checkSufficientSessions(sessions: Iterable<FlowSession>, vararg states: ContractState)

@Suspendable
fun FlowLogic<*>.checkSufficientSessions(sessions: Iterable<FlowSession>, transaction: TransactionBuilder)
```

#### Remarks

Assuming that the specified states will be used as input or output states in a transaction, this function will extract a set of all state participants, excluding identities owned by the initiating node, and then check that a flow session exists for each participant.

---

### findTransaction *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Finds a recorded transaction in the vault for the specified transaction hash.

```kotlin
@Suspendable
fun FlowLogic<*>.findTransaction(transactionHash: SecureHash): SignedTransaction

@Suspendable
fun FlowLogic<*>.findTransaction(stateRef: StateRef): SignedTransaction

@Suspendable
fun FlowLogic<*>.findTransaction(stateAndRef: StateAndRef<*>): SignedTransaction
```

---

### DEFAULT_SORTING *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

The default sorting order.

```kotlin
val DEFAULT_SORTING: Sort
```

---

### DEFAULT_PAGE_SPECIFICATION *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

The default page specification.

```kotlin
val DEFAULT_PAGE_SPECIFICATION: PageSpecification
```

---

### MAXIMUM_PAGE_SPECIFICATION *Extension Property*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

The maximum page specification.

```kotlin
val MAXIMUM_PAGE_SPECIFICATION: PageSpecification
```

---

### andWithExpressions *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Builds a custom query criteria. This combines all non-null query expressions using logical AND.

```kotlin
fun <T : StatePersistable> QueryCriteria.VaultQueryCriteria.andWithExpressions(
    vararg expressions: CriteriaExpression<T, Boolean>?
): QueryCriteria
```

---

### orWithExpressions *Extension Function*

**Module:** onixlabs-corda-core-workflow

**Package:** io.onixlabs.corda.core.workflow

#### Description

Builds a custom query criteria. This combines all non-null query expressions using logical OR.

```kotlin
fun <T : StatePersistable> QueryCriteria.VaultQueryCriteria.andWithExpressions(
    vararg expressions: CriteriaExpression<T, Boolean>?
): QueryCriteria
```

---

### RPCService *Abstract Class*

**Module:** onixlabs-corda-core-integration

**Package:** io.onixlabs.corda.core.integration

#### Description

Represents the base class for implementing integration services over RPC.

```kotlin
abstract class RPCService(val rpc: CordaRPCOps)
```

---

### MessageService *Class*

**Module:** onixlabs-corda-core-integration

**Package:** io.onixlabs.corda.core.integration

#### Description

Represents an RPC service for sending and subscribing to transient messages.

```kotlin
class MessageService(rpc: CordaRPCOps) : RPCService(rpc)
```

---

### MessageAcknowledgementService *Class*

**Module:** onixlabs-corda-core-integration

**Package:** io.onixlabs.corda.core.integration

#### Description

Represents an RPC service for sending and subscribing to transience message acknowledgements.

```kotlin
class MessageAcknowledgementService(rpc: CordaRPCOps) : RPCService(rpc)
```

---

### TransactionNoteService *Class*

**Module:** onixlabs-corda-core-integration

**Package:** io.onixlabs.corda.core.integration

#### Description

Represents an RPC service for adding, sending and persisting transaction notes.

```kotlin
class TransactionNoteService(rpc: CordaRPCOps) : RPCService(rpc)
```

---

