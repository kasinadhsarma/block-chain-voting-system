package com.votex.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class HashUtil {
    
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String getMerkleRoot(List<String> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "";
        }
        
        List<String> treeLayer = transactions.stream()
            .map(HashUtil::applySha256)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
        while (treeLayer.size() > 1) {
            List<String> newLayer = new ArrayList<>();
            
            for (int i = 0; i < treeLayer.size(); i += 2) {
                if (i + 1 < treeLayer.size()) {
                    newLayer.add(applySha256(treeLayer.get(i) + treeLayer.get(i + 1)));
                } else {
                    newLayer.add(applySha256(treeLayer.get(i) + treeLayer.get(i)));
                }
            }
            
            treeLayer = newLayer;
        }
        
        return treeLayer.isEmpty() ? "" : treeLayer.get(0);
    }
}