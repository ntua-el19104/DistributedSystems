package gr.ntua.communication.rabbitMQCommunication.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
public class CommunicationUtils {
    public static PublicKey fromBytesToPK(byte[] publicKeyBytes){
        PublicKey receivedPublicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            receivedPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            return receivedPublicKey;
        } catch (Exception e) {
            log.error(e + "Error in decoding received public key");
            return null;
        }
    }

}
