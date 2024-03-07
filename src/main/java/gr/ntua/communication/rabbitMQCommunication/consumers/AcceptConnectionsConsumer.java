package gr.ntua.communication.rabbitMQCommunication.consumers;

import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import gr.ntua.communication.rabbitMQCommunication.entities.ConnectionReply;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
@Setter
public class AcceptConnectionsConsumer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SharedConfig sharedConfig;


    private int networkSize = 0;

    @RabbitListener(queues = MQConfig.CONNECTION_QUEUE)
    public void bootstrapListener(String senderNodePublicKey){
        if (sharedConfig.isBootstrap()){
            System.out.println(senderNodePublicKey);
            networkSize++;
            //add NodeInfo to the list of nodes
            int id = networkSize;
            rabbitTemplate.convertAndSend(MQConfig.EXCHANGE,MQConfig.ACCEPTED_CONNECTIONS_ROUTING_KEY, new ConnectionReply(id, senderNodePublicKey));
            //if id == networkSize then broadcast the NodeInfo of all nodes and
            //the blockchain to all nodes
        }
    }

    @RabbitListener(queues = MQConfig.ACCEPTED_CONNECTIONS_QUEUE)
    public void receiveConnectionReply(ConnectionReply connectionReply) {
        //if (connectionReply.getPublicKey() == sharedConfig.getPublicKey())
        //{
        System.out.println("Listened to a reply");
            try {
                int receivedId = connectionReply.getNodeId();
                //receivedIdFuture.complete(receivedId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                //receivedIdFuture.completeExceptionally(e);
            }
        //}
    }
}