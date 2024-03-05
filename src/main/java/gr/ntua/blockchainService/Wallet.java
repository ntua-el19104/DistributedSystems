package gr.ntua.blockchainService;

import gr.ntua.utils.TransactionUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;
import java.util.Base64;

public class Wallet {

    private PrivateKey privateKey;

    private PublicKey publicKey;

    public Wallet() {
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }


    public byte[] generateSign(byte[] data) {
        try {
            return TransactionUtils.signTransaction(privateKey, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

     public static String getKeyToString(Key key) {
         return Base64.getEncoder().encodeToString(key.getEncoded());
     }
    @Override
    public String toString() {
        return "Public Key: " + getKeyToString(publicKey).substring(0, 20) + "..." +
                "\nPrivate Key: " + getKeyToString(privateKey).substring(0, 20) + "...";
    }
}
