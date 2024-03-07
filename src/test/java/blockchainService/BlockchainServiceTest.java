package blockchainService;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Node;
import gr.ntua.communication.ClassInstancesCommunication;
import gr.ntua.communication.Communication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockchainServiceTest {

    private final int NUMBER_OF_NODES = 10;

    private ClassInstancesCommunication communication;


    @BeforeEach
    public void blockchainSetup() {
        communication = new ClassInstancesCommunication();
        for(int i=0; i < NUMBER_OF_NODES; i++) {
            Node node = new Node(communication);
            communication.addNode(node);
            node.connectToBlockchat();
        }
        communication.broadcastAddresses();
    }

    @Test
    public void test() {
        final Node bootstrapNode = communication.getNodesList().get(0);
        assertEquals(communication.getNodesList().size(), NUMBER_OF_NODES);
        for(int i=0; i<NUMBER_OF_NODES; i++) {
            System.out.println(communication.getNodesList().get(i));
        }

        Block genesis = bootstrapNode.createGenesisBlock();
        communication.broadcastBlock(genesis,-1);
        for(int i=0; i<NUMBER_OF_NODES; i++) {
            System.out.println(communication.getNodesList().get(i).getBlockchain());
        }
    }
}