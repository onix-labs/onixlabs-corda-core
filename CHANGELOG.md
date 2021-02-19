![ONIX Labs](https://raw.githubusercontent.com/onix-labs/onix-labs.github.io/master/content/logo/master_full_md.png)

# Change Log

This document serves as the change log for the ONIXLabs Corda Core API.

## Version 1.2.0

### Contract

#### ContractID (interface)

Defines the contract ID for a Corda contract. Rather than referencing the contract by a string or by its canonical name, implementing `ContractID` on a companion object adds the contract ID to the class for you automatically.

#### SignedCommandData (interface)

Defines a Corda contract command that maintains verifiable signature data. This can be used within a contract to check that a particular contract participant signed over the command - usually the transaction initiator.

#### VerifiedCommandData (interface)

Defines a Corda contract command that that localises its verification. Rather than commands simply being marker objects to determine which verification to execute, this leans towards the single responsibility principle, whereby each command is responsible for its verification.

#### SignatureData (class)

Represents a composite of both signed and unsigned data, which can be verified with a public key.

### Workflow

#### Extensions

-   Moved to new extension naming convention for maintainability.
-   Added extensions to filter a set of sessions to include or exclude certain counter-parties, or state participants.

## Version 1.1.0

### Contract

#### Extensions

-   Moved to new extension file naming convention for maintainability.
-   Added extensions to obtain single inputs, reference inputs and outputs from a `LedgerTransaction`.
-   Added extension to cast `Iterable<StateAndRef<*>>` to `List<StateAndRef<T>>`.

### Workflow

#### Extensions

-   Moved to new extension naming convention for maintainability.
-   Added extensions to filter a set of sessions to include or exclude certain counter-parties, or state participants.

## Version 1.0.0

### Contract

#### ChainState (interface)

Defines a Corda chain state. 

>   A chain state is similar to a `LinearState`, in that it represents a set of state transitions. Chain states adopt a concept similar to blockchain, where each new state transition references the previous one by it's `StateRef`, or null if it's the first state in the chain.

#### Hashable (interface)

Defines an object that can produce a unique hash. 

>   This is useful if you require some level of state uniqueness.

#### Resolvable (interface)

Defines an object which resolves a `ContractState`. 

>   This is the fundamental principle on which state pointers are implemented in Corda; their ability to maintain and resolve a reference back to another known state in the vault. The implementation here is flexible and open, allowing developers to model resolvable as a design pattern, rather than as a strict implementation detail.

#### TransactionResolution (enum)

Defines how to resolve states from a transaction, and is used by the `Resolvable` interface.

#### Extensions

-   Added extension to obtain a set of owning keys from a collection of `AbstractParty`.
-   Added extension to obtain a participant hash to determine state uniqueness by participation.
-   Added extensions to determine whether a chain state instance is pointing to a specified `StateRef`.
-   Added extension to cast an unknown `StateAndRef<*>` to a typed `StateAndRef<T>`.

### Workflow

#### FindStateFlow (abstract class)

Represents the base class for implementing a query that finds a single state.

#### FindStatesFlow (abstract class)

Represents the base class for implementing a query that finds multiple states.

>   This design pattern enables developers to be more consistent in their approach to performing vault queries, regardless of whether they're being performed from within the node (using `ServiceHub`) or from an RPC client (using `CordaRPCOps`).

#### Message (open class)

Represents an open, transient message.

#### MessageAcknowledgement (open class)

Represents an open acknowledgement to a message.

#### SendMessageFlow (class)

Sends a message to other nodes on the Corda network.

#### ReceiveMessageFlow (class)

Receives a message from another node on the Corda network.

#### SendMessageAcknowledgementFlow (class)

Sends a message acknowledgement to other nodes on the Corda network.

#### ReceiveMessageAcknowledgementFlow (class)

Receives a message acknowledgement from another node on the Corda network.

#### TransactionNote (class)

Represents a wrapper around a transaction note and the transaction that the note belongs to.

#### SendTransactionNoteFlow (class)

Sends a transaction note to other nodes on the Corda network for persistence.

#### ReceiveTransactionNoteFlow (class)

Receives a transaction note from another node on the Corda network, and optionally persists it.

>   All messaging APIs are part of the ONIXLabs Corda Code messaging protocol, which allows Corda nodes to send and receive transient messages and messages acknowledgements, which are not bound to the ledger.

#### Extensions

-   Added extensions to obtain the first notary from the network map cache.
-   Added extensions to obtain a random notary from the network map cache.
-   Added extensions to obtain the preferred notary from the CorDapp config file.
-   Added extensions to set, and optionally log progress tracker steps.
-   Added extensions to initiate flow sessions for a collection of parties and/or state participants.
-   Added extensions to check that sufficient flow sessions have been passed for the specified states.
-   Added extensions to find transactions by transaction ID or `StateRef`.
-   Added constants for default sorting and default, or maximum size page specification.
-   Added extensions to  build complex query expressions.

### Integration

#### RPCService (abstract class)

Represents the base class for implementing services that utilize `CordaRPCOps`.

#### MessageService (class)

Represents an RPC service for sending and subscribing to transient messages.

#### MessageAcknowledgementService (class)

Represents an RPC service for sending and subscribing to transience message acknowledgements.

#### TransactionNoteService (class)

Represents an RPC service for adding, sending and persisting transaction notes.



