package com.votex.blockchain;

import com.votex.model.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BlockchainTest {
    
    private Blockchain blockchain;
    
    @BeforeEach
    public void setUp() {
        blockchain = new Blockchain(1); // Use lower difficulty for faster tests
    }
    
    @Test
    public void testBlockchainCreation() {
        // Test default constructor
        Blockchain blockchain = new Blockchain();
        assertNotNull(blockchain);
        assertNotNull(blockchain.getChain());
        assertEquals(1, blockchain.getChain().size()); // Genesis block should be created
        assertEquals(4, blockchain.getDifficulty()); // Default difficulty
        
        // Test constructor with custom difficulty
        int customDifficulty = 2;
        Blockchain customBlockchain = new Blockchain(customDifficulty);
        assertEquals(customDifficulty, customBlockchain.getDifficulty());
    }

    @Test
    public void testGenesisBlockCreation() {
        // Verify genesis block properties
        Block genesisBlock = blockchain.getChain().get(0);
        assertEquals(0, genesisBlock.getIndex());
        assertEquals("0", genesisBlock.getPreviousHash());
        assertNotNull(genesisBlock.getHash());
    }

    @Test
    public void testAddBlock() {
        // Create a new block
        Block newBlock = new Block();
        List<String> transactions = Arrays.asList("transaction1", "transaction2");
        newBlock.setTransactions(transactions);
        
        // Add block to chain
        blockchain.addBlock(newBlock);
        
        // Verify chain size
        assertEquals(2, blockchain.getChain().size());
        
        // Verify block properties
        Block addedBlock = blockchain.getChain().get(1);
        assertEquals(1, addedBlock.getIndex());
        assertEquals(blockchain.getChain().get(0).getHash(), addedBlock.getPreviousHash());
        assertEquals(transactions, addedBlock.getTransactions());
    }

    @Test
    public void testAddBlockWithTransactions() {
        // Add transactions directly
        List<String> transactions = Arrays.asList("transaction1", "transaction2");
        blockchain.addBlock(transactions);
        
        // Verify chain size
        assertEquals(2, blockchain.getChain().size());
        
        // Verify block properties
        Block addedBlock = blockchain.getChain().get(1);
        assertEquals(1, addedBlock.getIndex());
        assertEquals(blockchain.getChain().get(0).getHash(), addedBlock.getPreviousHash());
        assertEquals(transactions, addedBlock.getTransactions());
    }

    @Test
    public void testGetLatestBlock() {
        // Initially, latest block should be genesis block
        Block latestBlock = blockchain.getLatestBlock();
        assertEquals(0, latestBlock.getIndex());
        
        // Add a new block
        List<String> transactions = Arrays.asList("transaction1");
        blockchain.addBlock(transactions);
        
        // Now latest block should be the newly added block
        latestBlock = blockchain.getLatestBlock();
        assertEquals(1, latestBlock.getIndex());
        assertEquals(transactions, latestBlock.getTransactions());
    }

    @Test
    public void testIsChainValid() {
        // Initially, chain should be valid
        assertTrue(blockchain.isChainValid());
        
        // Add a couple of blocks
        blockchain.addBlock(Arrays.asList("transaction1"));
        blockchain.addBlock(Arrays.asList("transaction2"));
        
        // Chain should still be valid
        assertTrue(blockchain.isChainValid());
        
        // Tamper with a block
        Block block = blockchain.getChain().get(1);
        
        // Get the original hash before tampering
        String originalHash = block.getHash();
        
        // Tamper with transactions but don't update the hash
        List<String> mutableTransactions = new ArrayList<>(block.getTransactions());
        mutableTransactions.set(0, "tampered_transaction");
        block.setTransactions(mutableTransactions);
        
        // Force the block to keep its original hash (as if an attacker did this)
        block.setHash(originalHash);
        
        // Chain should now be invalid
        assertFalse(blockchain.isChainValid());
    }

    @Test
    public void testMineBlock() {
        // Create a block to mine
        Block block = new Block();
        block.setTransactions(Arrays.asList("transaction1"));
        
        // Mine the block
        blockchain.mineBlock(block);
        
        // Check if the hash has the required number of leading zeros
        String target = new String(new char[blockchain.getDifficulty()]).replace('\0', '0');
        assertTrue(block.getHash().substring(0, blockchain.getDifficulty()).equals(target), 
                "Mined block hash should start with the target number of zeros");
    }
    
    @Test
    public void testAddVoteToBlockchain() {
        // Create a vote
        Vote vote = new Vote();
        vote.setVoterId("voter123");
        vote.setCandidateId("candidate456");
        vote.setElectionId("election789");
        vote.setTimestamp(LocalDateTime.now());
        vote.setSignature("signature123");
        
        // Convert the vote to a transaction string
        String voteTransaction = vote.toString();
        
        // Add the vote transaction to the blockchain
        blockchain.addBlock(Collections.singletonList(voteTransaction));
        
        // Check vote was included in the chain
        Block minedBlock = blockchain.getChain().get(1);
        assertTrue(minedBlock.getTransactions().contains(voteTransaction));
    }
    
    @Test
    public void testMultipleTransactionsInBlock() {
        // Add multiple transactions
        List<String> transactions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            transactions.add("transaction" + i);
        }
        
        // Add to blockchain
        blockchain.addBlock(transactions);
        
        // Get the mined block
        Block minedBlock = blockchain.getChain().get(1);
        
        // Check that mined block has all transactions
        assertEquals(5, minedBlock.getTransactions().size());
        
        // Check that mined block hash starts with zeros based on difficulty
        assertTrue(minedBlock.getHash().startsWith(
            new String(new char[blockchain.getDifficulty()]).replace('\0', '0')
        ));
    }
    
    @Test
    public void testChainValidity() {
        // Add transactions and mine
        blockchain.addBlock(Arrays.asList("transaction1"));
        
        // Add more transactions and mine another block
        blockchain.addBlock(Arrays.asList("transaction2"));
        
        // Chain should be valid at this point
        assertTrue(blockchain.isChainValid());
        
        // Tamper with a block to invalidate the chain
        Block tamperedBlock = blockchain.getChain().get(1);
        String originalHash = tamperedBlock.getHash();
        
        // Create a new mutable list of transactions from the original block
        List<String> mutableTransactions = new ArrayList<>(tamperedBlock.getTransactions());
        mutableTransactions.add("fraudulent transaction");
        
        // Set the modified transactions back to the block
        tamperedBlock.setTransactions(mutableTransactions);
        
        // Keep the original hash (as if an attacker did this)
        tamperedBlock.setHash(originalHash);
        
        // Chain should now be invalid due to merkleRoot mismatch
        assertFalse(blockchain.isChainValid());
    }
    
    @Test
    public void testBlockchainGrowth() {
        assertEquals(1, blockchain.getChain().size()); // Genesis block
        
        // Mine multiple blocks
        for (int i = 0; i < 5; i++) {
            blockchain.addBlock(Arrays.asList("transaction for block " + (i+1)));
        }
        
        // Should have genesis block + 5 mined blocks
        assertEquals(6, blockchain.getChain().size());
        
        // Verify indices
        for (int i = 0; i < 6; i++) {
            assertEquals(i, blockchain.getChain().get(i).getIndex());
        }
        
        // Verify linking
        for (int i = 1; i < 6; i++) {
            Block currentBlock = blockchain.getChain().get(i);
            Block previousBlock = blockchain.getChain().get(i-1);
            assertEquals(previousBlock.getHash(), currentBlock.getPreviousHash());
        }
    }
}