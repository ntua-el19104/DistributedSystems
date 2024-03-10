package gr.ntua.communication.rabbitMQCommunication.configurations;

import gr.ntua.blockchainService.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;

@Component
@Setter
@Getter
@NoArgsConstructor
public class SharedConfig {
    private PublicKey nodePublicKey;
    private Node node;
    private CompletableFuture<Integer> receivedId = new CompletableFuture<>();


    public void setNodeId(int id){
        receivedId.complete(id);
    }
}
