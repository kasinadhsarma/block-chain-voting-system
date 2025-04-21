package com.votex.blockchain;

import com.votex.crypto.HashUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Block {
    private int index;
    private long timestamp;
    private List<String> transactions;
    private String previousHash;
    private String hash;
    private int nonce;
    private String merkleRoot;

    public Block() {
        this.transactions = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0;
        this.merkleRoot = ""; // Initialize with empty string
        this.hash = calculateHash(); // Initialize hash
    }

    public Block(int index, long timestamp, List<String> transactions, String previousHash, String merkleRoot, int nonce) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.merkleRoot = merkleRoot != null ? merkleRoot : "";
        this.nonce = nonce;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        // Always update merkleRoot when calculating hash if we have transactions
        // This ensures the hash reflects the current state of transactions
        if (transactions != null && !transactions.isEmpty()) {
            merkleRoot = HashUtil.getMerkleRoot(transactions);
        }
        
        String dataToHash = index + timestamp + (merkleRoot != null ? merkleRoot : "") + 
                          (previousHash != null ? previousHash : "") + nonce;
        return HashUtil.applySha256(dataToHash);
    }

    public void mineBlock(int difficulty) {
        merkleRoot = HashUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        
        // Initialize hash if null
        if (hash == null) {
            hash = calculateHash();
        }
        
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined! Hash: " + hash);
    }

    public void addTransaction(String transaction) {
        if (transaction == null) {
            return;
        }
        if ((previousHash != null && !previousHash.equals("0"))) {
            transactions.add(transaction);
        }
    }

    // Getters and Setters
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<String> transactions) {
        this.transactions = transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }
}