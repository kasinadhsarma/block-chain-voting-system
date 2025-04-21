package com.votex.crypto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HashUtilTest {

    @Test
    public void testApplySha256() {
        // Test with a known input and expected output
        String input = "test";
        String expectedHash = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
        String actualHash = HashUtil.applySha256(input);
        
        assertEquals(expectedHash, actualHash, "SHA-256 hash should match the expected value");
    }

    @Test
    public void testApplySha256WithEmptyString() {
        // Test with empty string
        String input = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        String actualHash = HashUtil.applySha256(input);
        
        assertEquals(expectedHash, actualHash, "SHA-256 hash of empty string should match expected value");
    }

    @Test
    public void testGetMerkleRootWithSingleTransaction() {
        // Test with a single transaction
        List<String> transactions = Arrays.asList("transaction1");
        String expected = HashUtil.applySha256("transaction1");
        String actual = HashUtil.getMerkleRoot(transactions);
        
        assertEquals(expected, actual, "Merkle root of single transaction should be its SHA-256 hash");
    }

    @Test
    public void testGetMerkleRootWithMultipleTransactions() {
        // Test with multiple transactions
        List<String> transactions = Arrays.asList("transaction1", "transaction2", "transaction3", "transaction4");
        String tx1Hash = HashUtil.applySha256("transaction1");
        String tx2Hash = HashUtil.applySha256("transaction2");
        String tx3Hash = HashUtil.applySha256("transaction3");
        String tx4Hash = HashUtil.applySha256("transaction4");
        
        String hashLevel1_1 = HashUtil.applySha256(tx1Hash + tx2Hash);
        String hashLevel1_2 = HashUtil.applySha256(tx3Hash + tx4Hash);
        String expected = HashUtil.applySha256(hashLevel1_1 + hashLevel1_2);
        
        String actual = HashUtil.getMerkleRoot(transactions);
        
        assertEquals(expected, actual, "Merkle root of multiple transactions should be calculated correctly");
    }

    @Test
    public void testGetMerkleRootWithEmptyList() {
        // Test with an empty list
        List<String> transactions = Arrays.asList();
        String actual = HashUtil.getMerkleRoot(transactions);
        
        assertEquals("", actual, "Merkle root of empty list should be an empty string");
    }

    @Test
    public void testGetMerkleRootWithOddNumberOfTransactions() {
        // Test with odd number of transactions
        List<String> transactions = Arrays.asList("transaction1", "transaction2", "transaction3");
        String tx1Hash = HashUtil.applySha256("transaction1");
        String tx2Hash = HashUtil.applySha256("transaction2");
        String tx3Hash = HashUtil.applySha256("transaction3");
        
        String hashLevel1_1 = HashUtil.applySha256(tx1Hash + tx2Hash);
        String hashLevel1_2 = HashUtil.applySha256(tx3Hash + tx3Hash); // Duplicate last element for odd number
        String expected = HashUtil.applySha256(hashLevel1_1 + hashLevel1_2);
        
        String actual = HashUtil.getMerkleRoot(transactions);
        
        assertEquals(expected, actual, "Merkle root of odd number of transactions should be calculated correctly");
    }
}