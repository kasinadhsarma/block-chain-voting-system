# 🔗 `Blockchain` Class - Votex Blockchain Voting System

The `Blockchain` class manages the ledger of blocks, enforces consensus rules (proof-of-work), and ensures data immutability. It forms the backbone of the Votex blockchain voting platform.

---

## 📦 Package
```java
package com.votex.blockchain;
```

---

## 📁 Imports
```java
import com.votex.crypto.HashUtil;
import com.votex.model.Vote;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
```

---

## 🧩 Fields

| Field | Type | Description |
|-------|------|-------------|
| `chain` | `List<Block>` | List representing the blockchain (ordered list of blocks) |
| `difficulty` | `int` | Mining difficulty level (number of leading zeros required in hash) |

---

## 🛠️ Constructors

### `Blockchain()`
- Initializes a new blockchain with default difficulty `4`.
- Automatically creates and appends a **genesis block** (first block).

### `Blockchain(int difficulty)`
- Initializes a new blockchain with a custom mining difficulty.

---

## 🧱 Key Methods

### `private void createGenesisBlock()`
- Creates the first block in the chain with:
  - Index = `0`
  - Previous hash = `"0"`
  - Empty Merkle root
  - Nonce = `0`
  - Hash is computed and assigned

---

### `Block getLatestBlock()`
- Returns the most recent block in the chain.

---

### `void addBlock(Block newBlock)`
- Links a new block to the blockchain:
  - Sets its index and previous hash
  - Mines the block using the difficulty level
  - Appends it to the chain

---

### `void addBlock(List<String> transactions)`
- Creates a block from a list of transaction strings and adds it to the chain.

---

### `void mineBlock(Block block)`
- Proof-of-work algorithm:
  - Repeatedly increments nonce and recalculates the block’s hash
  - Stops once hash meets the difficulty (e.g., starts with `"0000"`)

> This method is usually **invoked inside `addBlock`** and is similar to `Block.mineBlock()`.

---

### `boolean isChainValid()`
- Validates the blockchain integrity:
  - Checks if each block’s hash matches its calculated hash
  - Verifies each block’s link to the previous block
  - Ensures Merkle root corresponds to stored transactions (prevents tampering)

---

## 📥 Getters & Setters

| Method | Purpose |
|--------|---------|
| `getChain()` / `setChain(List<Block>)` | Access or modify the entire blockchain |
| `getDifficulty()` / `setDifficulty(int)` | Get or set mining difficulty |

---

## ✅ Example Usage
```java
Blockchain blockchain = new Blockchain();

List<String> transactions = new ArrayList<>();
transactions.add("VoterA -> CandidateX");
transactions.add("VoterB -> CandidateY");

blockchain.addBlock(transactions);

System.out.println("Blockchain valid: " + blockchain.isChainValid());
```

---

## 🧠 Notes
- The blockchain uses **proof-of-work (PoW)** for consensus.
- The integrity of votes is ensured by:
  - SHA-256 hashing
  - Merkle root verification
- Tampering with data in any block invalidates the entire chain.
