package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.CliClient;
import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import gr.ntua.utils.TransactionUtils;
import gr.ntua.utils.ViewBlockchain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Slf4j
@SpringBootApplication
public class FileApplication {

    private static SharedConfig sharedConfig = null;
    private static RabbitMQCommunication rabbitMQCommunication = null;

    @Autowired
    public FileApplication(SharedConfig sharedConfig, RabbitMQCommunication rabbitMQCommunication) {
        FileApplication.sharedConfig = sharedConfig;
        FileApplication.rabbitMQCommunication = rabbitMQCommunication;
    }
    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);
        boolean isBootstrap = Boolean.parseBoolean(args[0]);
        int maxNetworkSize = Integer.parseInt(args[1]);

        Node node = new Node(rabbitMQCommunication, isBootstrap);
        sharedConfig.setNode(node);
        sharedConfig.setMaxNetworkSize(maxNetworkSize);
        node.connectToBlockchat();
        System.out.println("Node has id: " + node.getId());

        if (isBootstrap) {
            try {
                sharedConfig.getAllNodesConnected().get();
                rabbitMQCommunication.broadcastAddresses();
                Block genesis = node.createGenesisBlock();
                rabbitMQCommunication.broadcastBlock(genesis, -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            sharedConfig.getReceivedGenesisBlock().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = "src/main/java/gr/ntua/input/trans" + node.getId() + ".txt";
        List<Transaction> list = TransactionUtils.textToTransactions(node,path);
        for(Transaction t:list){
            rabbitMQCommunication.broadcastTransaction(t);
        }
        CliClient cliClient = new CliClient(node, rabbitMQCommunication);
        cliClient.run();
    }
}
