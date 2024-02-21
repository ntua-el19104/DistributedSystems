package gr.ntua;

import gr.ntua.utils.TransactionUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
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

    @Override
    public String toString() {
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return "Public Key: " + publicKeyString.substring(0, 20) + "..." +
                "\nPrivate Key: " + privateKeyString.substring(0, 20) + "...";
    }
}
