# 🧱 `Block` Class - Votex Blockchain Voting System

The `Block` class defines the core structure of a block in the Votex blockchain. Each block encapsulates transactional data, cryptographic hashes, and links to previous blocks to ensure security and immutability.

---

## 📦 Package
```java
package com.votex.blockchain;
```

---

## 🧩 Fields

| Field | Type | Description |
|-------|------|-------------|
| `index` | `int` | Position of the block in the blockchain |
| `timestamp` | `long` | Unix timestamp of block creation |
| `transactions` | `List<String>` | List of transaction data stored in the block |
| `previousHash` | `String` | Hash of the previous block, enabling chaining |
| `hash` | `String` | Current block’s SHA-256 hash |
| `nonce` | `int` | Value used in mining (proof-of-work) |
| `merkleRoot` | `String` | Root hash of all transactions (Merkle Tree) |

---

## 🔧 Constructors

### `Block()`
- Default constructor that initializes:
  - `timestamp` with the current time
  - `transactions` as an empty list
  - `nonce` as `0`
  - `hash` is calculated using the `calculateHash()` method

### `Block(int index, long timestamp, List<String> transactions, String previousHash, String merkleRoot, int nonce)`
- Full constructor to initialize all block fields manually and compute its hash.

---

## 🔐 Methods

### `String calculateHash()`
- Calculates the block’s hash using SHA-256 over:
  - `index`, `timestamp`, `merkleRoot`, `previousHash`, `nonce`
- Automatically updates the `merkleRoot` if transactions exist.

### `void mineBlock(int difficulty)`
- Proof-of-work mining algorithm.
- Keeps incrementing `nonce` until a hash is found that matches the difficulty target (prefix of zeros).
- Logs the mined hash once found.

### `void addTransaction(String transaction)`
- Adds a transaction to the block if:
  - It's not null
  - The block is not a genesis block (i.e., `previousHash` is not `"0"`)

---

## 📥 Getters & Setters
Includes standard getters and setters for:
- `index`
- `timestamp`
- `transactions`
- `previousHash`
- `hash`
- `nonce`
- `merkleRoot`

---

## 📚 Dependencies

### `HashUtil`
Used for:
- Generating SHA-256 hash: `applySha256(String input)`
- Calculating Merkle root from a list of transactions: `getMerkleRoot(List<String> txns)`

---

## 🔄 Example Usage
```java
Block genesisBlock = new Block();
genesisBlock.setIndex(0);
genesisBlock.setPreviousHash("0");
genesisBlock.addTransaction("VoterA -> Voted for CandidateX");
genesisBlock.mineBlock(4); // Difficulty = 4 leading zeros
```

---

## ✅ Best Practices
- Ensure transactions are validated before adding.
- Always mine blocks before adding them to the blockchain.
- Genesis blocks should have a `previousHash` of `"0"`.
