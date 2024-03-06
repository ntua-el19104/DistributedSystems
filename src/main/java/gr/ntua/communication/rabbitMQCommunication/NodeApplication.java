package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.blockchainService.Node;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class NodeApplication {

    private static RabbitTemplate rabbitTemplate = null;

    @Autowired
    public NodeApplication(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);
        RabbitMQCommunication rabbitMQCommunication = new RabbitMQCommunication(rabbitTemplate);
        Node node = new Node(rabbitMQCommunication);
        if (Integer.parseInt(args[0]) != 0)
            node.connectToBlockchat();
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(AcceptConnectionsConsumer acceptConnectionsConsumer) {
//        return args -> {
//            acceptConnectionsConsumer.setNodeId(Integer.parseInt(args[0]));
//        };
//    }

}
