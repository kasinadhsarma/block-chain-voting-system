package com.votex.blockchain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockTest {

    @Test
    public void testBlockCreation() {
        int index = 1;
        long timestamp = System.currentTimeMillis();
        List<String> transactions = new ArrayList<>(Arrays.asList("tx1", "tx2"));
        String previousHash = "0000abcd";
        int nonce = 0;
        
        Block block = new Block(index, timestamp, transactions, previousHash, "", nonce);
        
        assertEquals(index, block.getIndex());
        assertEquals(timestamp, block.getTimestamp());
        assertEquals(transactions, block.getTransactions());
        assertEquals(previousHash, block.getPreviousHash());
        assertEquals(0, block.getNonce());
    }
    
    @Test
    public void testCalculateHash() {
        // Create a block with known values
        Block block = new Block();
        block.setIndex(1);
        block.setTimestamp(1000L);
        block.setMerkleRoot("merkleRoot");
        block.setPreviousHash("prevHash");
        block.setNonce(0);
        
        // Calculate hash
        String hash = block.calculateHash();
        
        // Recalculate hash to verify
        String expectedHash = block.calculateHash();
        
        assertEquals(expectedHash, hash, "Hash calculation should be deterministic");
        
        // Change a value and make sure hash changes
        block.setNonce(1);
        String newHash = block.calculateHash();
        
        assertNotEquals(hash, newHash, "Hash should change when block data changes");
    }

    @Test
    public void testHashConsistency() {
        // Create two identical blocks
        Block block1 = new Block(1, 1619712345000L, 
                                 new ArrayList<>(Arrays.asList("tx1", "tx2")), 
                                 "0000abcd", "", 0);
        
        Block block2 = new Block(1, 1619712345000L, 
                                 new ArrayList<>(Arrays.asList("tx1", "tx2")), 
                                 "0000abcd", "", 0);
        
        // Force recalculation of hash to ensure merkleRoot is updated
        String hash1 = block1.calculateHash();
        String hash2 = block2.calculateHash();
        
        // Identical blocks should produce identical hashes
        assertEquals(hash1, hash2);
        
        // Change transaction data in block2
        block2.getTransactions().add("tx3");
        
        // Recalculate hash after changing transactions
        String newHash = block2.calculateHash();
        
        // Now hashes should be different
        assertNotEquals(hash1, newHash, "Hash should change when transactions change");
    }

    @Test
    public void testBlockConstructor() {
        // Test default constructor
        Block block = new Block();
        assertNotNull(block);
        assertNotNull(block.getTransactions());
        assertTrue(block.getTransactions().isEmpty());
        assertEquals(0, block.getNonce());
        
        // Test parameterized constructor
        int index = 1;
        long timestamp = System.currentTimeMillis();
        List<String> transactions = Arrays.asList("tx1", "tx2");
        String previousHash = "prevHash";
        String merkleRoot = "merkleRoot";
        int nonce = 5;
        
        Block paramBlock = new Block(index, timestamp, transactions, previousHash, merkleRoot, nonce);
        
        assertEquals(index, paramBlock.getIndex());
        assertEquals(timestamp, paramBlock.getTimestamp());
        assertEquals(transactions, paramBlock.getTransactions());
        assertEquals(previousHash, paramBlock.getPreviousHash());
        // Don't check the exact merkleRoot value as it's calculated from transactions
        assertNotNull(paramBlock.getMerkleRoot());
        assertEquals(nonce, paramBlock.getNonce());
        assertNotNull(paramBlock.getHash());
    }

    @Test
    public void testMineBlock() {
        Block block = new Block();
        block.setIndex(1);
        block.setTimestamp(1000L);
        block.setPreviousHash("prevHash");
        
        // Add transactions to calculate merkle root
        List<String> transactions = Arrays.asList("tx1", "tx2");
        block.setTransactions(transactions);
        
        // Set initial hash
        block.setHash(block.calculateHash());
        
        // Mining difficulty (number of leading zeros)
        int difficulty = 2;
        String target = new String(new char[difficulty]).replace('\0', '0');
        
        // Mine the block
        block.mineBlock(difficulty);
        
        // Check if the hash has the required number of leading zeros
        assertTrue(block.getHash().startsWith(target), 
                "Mined block hash should start with " + difficulty + " zeros");
    }

    @Test
    public void testAddTransaction() {
        Block block = new Block();
        
        // Cannot add transaction to genesis block (previousHash is null)
        block.addTransaction("tx1");
        assertTrue(block.getTransactions().isEmpty(), "Should not add transaction to genesis block");
        
        // Set previous hash and add transaction
        block.setPreviousHash("prevHash");
        block.addTransaction("tx1");
        assertEquals(1, block.getTransactions().size(), "Should add transaction when previousHash is set");
        assertEquals("tx1", block.getTransactions().get(0));
        
        // Add another transaction
        block.addTransaction("tx2");
        assertEquals(2, block.getTransactions().size());
        
        // Try adding null transaction
        block.addTransaction(null);
        assertEquals(2, block.getTransactions().size(), "Should not add null transaction");
    }
}