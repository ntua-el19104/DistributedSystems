package gr.ntua.communication.rabbitMQCommunication.consumers;

import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import gr.ntua.communication.rabbitMQCommunication.entities.ConnectionReply;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class AcceptConnectionsConsumer {

    private int nodeId;
    private PublicKey publicKey;

    //@Autowired
    //private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MQConfig.CONNECTION_QUEUE)
    public void bootstrapListener(String senderNodePublicKey){
        int nodeId = 0;
        if (nodeId == 0){
            System.out.println(senderNodePublicKey);
            //add NodeInfo to the list of nodes
            int id = 2;
            //rabbitTemplate.convertAndSend(MQConfig.EXCHANGE,MQConfig.ROUTING_KEY, new ConnectionReply(id,senderNodePublicKey));
            //if id == networkSize then broadcast the NodeInfo of all nodes and
            //the blockchain to all nodes
        }
    }

    //needs to be in a different queue!!!!!
    @RabbitListener(queues = MQConfig.CONNECTION_QUEUE)
    public void newNodeListenerAsync(ConnectionReply connectionReply) {
        if (connectionReply.getPublicKey() == this.publicKey){
            System.out.println(connectionReply);
            //set the id for this node
        }

    }



}