package gr.ntua.utils;

import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class TransactionUtils {
    public static byte[] signTransaction(PrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public static boolean verifySignature(PublicKey publicKey, byte[] data, byte[] signature) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(data);
        return publicSignature.verify(signature);
    }

    public static byte[] generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate transaction ID hash", e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static List<Transaction> textToTransactions(Node node,String path){
        List<Transaction> list = new ArrayList<>();
        int size = node.getAddresses().size();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();

            while (line != null) {
                int id = Integer.parseInt(line.substring(2,3));
                String message = line.substring(4);
                line = reader.readLine();
                if(id >= size)
                    continue;
                PublicKey publicKey = node.getAddresses().get(id);
                try{
                    list.add(node.createTransaction(0,publicKey,message));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
