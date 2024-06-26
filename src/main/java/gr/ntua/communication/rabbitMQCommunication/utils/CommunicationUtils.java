package gr.ntua.communication.rabbitMQCommunication.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommunicationUtils {

  public static PublicKey fromBytesToPK(byte[] publicKeyBytes) {
    PublicKey receivedPublicKey;
    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      receivedPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
      return receivedPublicKey;
    } catch (Exception e) {
      log.error(e + "Error in decoding received public key");
      throw new Error("Error in decoding received public key");
    }
  }

  public static byte[] fromPublicKeyListToBytes(List<PublicKey> publicKeyList) {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
        objectOutputStream.writeObject(publicKeyList);
      }
      return byteArrayOutputStream.toByteArray();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new Error("Could not serialize the list of public keys");
    }
  }

  public static List<PublicKey> fromBytesToPublicKeyList(byte[] publicKeyListBytes) {
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(publicKeyListBytes);
      ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
      return (List<PublicKey>) objectInputStream.readObject();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new Error("Could not deserialize bytes to list of public keys");
    }
  }

}
