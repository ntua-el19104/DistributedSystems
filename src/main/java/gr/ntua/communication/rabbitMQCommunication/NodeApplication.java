package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.blockchainService.Node;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NodeApplication {

    private static RabbitTemplate rabbitTemplate = null;
    private static boolean isBootstrap;
    private static SharedConfig sharedConfig = null;

    @Autowired
    public NodeApplication(RabbitTemplate rabbitTemplate, SharedConfig sharedConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.sharedConfig = sharedConfig;
    }



    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);
        isBootstrap = Boolean.parseBoolean(args[0]);
        sharedConfig.setBootstrap(isBootstrap);
        RabbitMQCommunication rabbitMQCommunication = new RabbitMQCommunication(rabbitTemplate, sharedConfig);
        Node node = new Node(rabbitMQCommunication);
        if (isBootstrap) {
            node.setIsBootstrap(true);
            node.setId(0);
        } else
            node.connectToBlockchat();

        System.out.println("Node has id:" + node.getId());

    }

    @PostConstruct
    public void init(){
        sharedConfig.setBootstrap(isBootstrap);
    }


}
