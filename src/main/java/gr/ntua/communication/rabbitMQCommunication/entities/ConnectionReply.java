package gr.ntua.communication.rabbitMQCommunication.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionReply {
    private int nodeId;
    private byte[] publicKey;
}
