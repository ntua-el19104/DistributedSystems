package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.blockchainService.Node;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NodeApplication {

    private static SharedConfig sharedConfig = null;
    private static RabbitMQCommunication rabbitMQCommunication = null;

    @Autowired
    public NodeApplication(SharedConfig sharedConfig, RabbitMQCommunication rabbitMQCommunication) {
        this.sharedConfig = sharedConfig;
        this.rabbitMQCommunication = rabbitMQCommunication;
    }


    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);
        Boolean isBootstrap = Boolean.parseBoolean(args[0]);
        Node node = new Node(rabbitMQCommunication, isBootstrap);
        sharedConfig.setNode(node);
        if (isBootstrap) {
            node.setId(0);
        } else
            node.connectToBlockchat();

        System.out.println("Node has id: " + node.getId());

    }

}
