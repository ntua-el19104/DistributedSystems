package gr.ntua.communication.rabbitMQCommunication.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.PublicKey;

@Data
@AllArgsConstructor
public class ConnectionReply {
    private int nodeId;
    private PublicKey publicKey;
}
