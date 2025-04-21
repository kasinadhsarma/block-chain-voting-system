package com.votex.crypto;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAUtil {
    
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(KEY_SIZE, new SecureRandom());
        return generator.generateKeyPair();
    }
    
    public static String getPublicKeyString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    public static String getPrivateKeyString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
    
    public static PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }
    
    public static PrivateKey getPrivateKeyFromString(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }
    
    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
    
    public static boolean verify(String data, String signatureString, PublicKey publicKey) throws Exception {
        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        
        return signature.verify(signatureBytes);
    }
}