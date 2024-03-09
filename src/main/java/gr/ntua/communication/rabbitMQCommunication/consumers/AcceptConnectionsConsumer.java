package gr.ntua.communication.rabbitMQCommunication.consumers;

import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import gr.ntua.communication.rabbitMQCommunication.entities.ConnectionReply;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Setter
public class AcceptConnectionsConsumer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SharedConfig sharedConfig;

    private int networkSize = 0;

    @RabbitListener(queues = "#{connectRequestQueue.name}")
    public void bootstrapListener(String senderNodePublicKey) {
        if (sharedConfig.isBootstrap()) {
            System.out.println(senderNodePublicKey);
            networkSize++;
            System.out.print("The network size now is:" + networkSize);
            //add NodeInfo to the list of nodes
            int id = networkSize;
            rabbitTemplate.convertAndSend(MQConfig.CONNECT_ACCEPT_EXCHANGE, "", new ConnectionReply(id, senderNodePublicKey));
            //if id == networkSize then broadcast the NodeInfo of all nodes and
            //the blockchain to all nodes
        }
    }

    @RabbitListener(queues = "#{connectAcceptQueue.name}")
    public void receiveConnectionReply(ConnectionReply connectionReply) {
        System.out.println("Listened to a reply for node" + connectionReply.getNodeId());
        if (connectionReply.getPublicKey().equals(sharedConfig.getPublicKey())) {
            try {
                int receivedId = connectionReply.getNodeId();
                //receivedIdFuture.complete(receivedId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                //receivedIdFuture.completeExceptionally(e);
            }
        }
    }
}