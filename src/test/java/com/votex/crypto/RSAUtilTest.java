package com.votex.crypto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAUtilTest {

    @Test
    public void testGenerateKeyPair() throws Exception {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        
        assertNotNull(keyPair, "KeyPair should not be null");
        assertNotNull(keyPair.getPrivate(), "Private key should not be null");
        assertNotNull(keyPair.getPublic(), "Public key should not be null");
    }

    @Test
    public void testEncryptionAndDecryption() throws Exception {
        // Generate a key pair
        KeyPair keyPair = RSAUtil.generateKeyPair();
        String originalMessage = "This is a test message";
        
        // Encrypt with public key
        String encryptedMessage = RSAUtil.encrypt(originalMessage, keyPair.getPublic());
        
        // Decrypt with private key
        String decryptedMessage = RSAUtil.decrypt(encryptedMessage, keyPair.getPrivate());
        
        assertEquals(originalMessage, decryptedMessage, "Decrypted message should match original");
        assertNotEquals(originalMessage, encryptedMessage, "Encrypted message should be different from original");
    }

    @Test
    public void testSignatureAndVerification() throws Exception {
        // Generate a key pair
        KeyPair keyPair = RSAUtil.generateKeyPair();
        String message = "This message needs to be signed";
        
        // Sign the message with private key
        String signature = RSAUtil.sign(message, keyPair.getPrivate());
        
        // Verify the signature with public key
        boolean isSignatureValid = RSAUtil.verify(message, signature, keyPair.getPublic());
        assertTrue(isSignatureValid, "Signature should be valid");
        
        // Verify with wrong message
        boolean isInvalidMessageSignatureValid = RSAUtil.verify("Wrong message", signature, keyPair.getPublic());
        assertFalse(isInvalidMessageSignatureValid, "Signature should be invalid for wrong message");
        
        // Generate another key pair and try to verify with its public key
        KeyPair anotherKeyPair = RSAUtil.generateKeyPair();
        boolean isWrongKeySignatureValid = RSAUtil.verify(message, signature, anotherKeyPair.getPublic());
        assertFalse(isWrongKeySignatureValid, "Signature should be invalid for wrong public key");
    }

    @Test
    public void testKeyConversion() throws Exception {
        // Generate a key pair
        KeyPair originalKeyPair = RSAUtil.generateKeyPair();
        
        // Convert keys to string
        String publicKeyString = RSAUtil.getPublicKeyString(originalKeyPair.getPublic());
        String privateKeyString = RSAUtil.getPrivateKeyString(originalKeyPair.getPrivate());
        
        // Convert back to key objects
        PublicKey reconstructedPublicKey = RSAUtil.getPublicKeyFromString(publicKeyString);
        PrivateKey reconstructedPrivateKey = RSAUtil.getPrivateKeyFromString(privateKeyString);
        
        // Compare encoded forms
        assertArrayEquals(
            originalKeyPair.getPublic().getEncoded(), 
            reconstructedPublicKey.getEncoded(),
            "Reconstructed public key should match original"
        );
        
        assertArrayEquals(
            originalKeyPair.getPrivate().getEncoded(), 
            reconstructedPrivateKey.getEncoded(),
            "Reconstructed private key should match original"
        );
    }
}