package gr.ntua.communication.rabbitMQCommunication.configurations;

import gr.ntua.blockchainService.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;

@Component
@Setter
@Getter
@NoArgsConstructor
public class SharedConfig {
    private PublicKey nodePublicKey;
    private Node node;
    //FOR BOOTSTRAP ===============
    private int maxNetworkSize;
    private CompletableFuture<Boolean> allNodesConnected = new CompletableFuture<>();
    //FOR REGULAR NODE ============
    private CompletableFuture<Integer> receivedId = new CompletableFuture<>();
    private CompletableFuture<List<PublicKey>> allPublicKeysList = new CompletableFuture<>();


    public void setNodeId(int id) {
        receivedId.complete(id);
    }

    public void allNodesConnectedComplete() {
        allNodesConnected.complete(true);
    }
}
