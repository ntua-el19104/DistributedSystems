package gr.ntua.communication.rabbitMQCommunication.consumers;

import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import gr.ntua.communication.rabbitMQCommunication.entities.ConnectionReply;
import gr.ntua.communication.rabbitMQCommunication.utils.CommunicationUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
@Setter
@Slf4j
public class AcceptConnectionsConsumer {

    private RabbitTemplate rabbitTemplate;
    private SharedConfig sharedConfig;
    private int networkSize;

    @Autowired
    public AcceptConnectionsConsumer(RabbitTemplate rabbitTemplate, SharedConfig sharedConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.sharedConfig = sharedConfig;
        this.networkSize = 0;
    }

    @RabbitListener(queues = "#{connectRequestQueue.name}")
    public void bootstrapListener(byte[] publicKeyBytes) {
        if (sharedConfig.getNode().isBootstrap()) {
            networkSize++;
            log.info("Bootstrap received another node request-the network size now is:" + networkSize);
            PublicKey receivedPublicKey = CommunicationUtils.fromBytesToPK(publicKeyBytes);
            sharedConfig.getNode().addAddress(receivedPublicKey);
            int id = networkSize;
            rabbitTemplate.convertAndSend(MQConfig.CONNECT_ACCEPT_EXCHANGE, "", new ConnectionReply(id, publicKeyBytes));
            //if id == networkSize then broadcast the NodeInfo of all nodes and
            //the blockchain to all nodes
        }
    }

    @RabbitListener(queues = "#{connectAcceptQueue.name}")
    public void receiveConnectionReply(ConnectionReply connectionReply) {
        PublicKey receivedPublicKey = CommunicationUtils.fromBytesToPK(connectionReply.getPublicKey());
        log.info("Received id for node with public key " + receivedPublicKey.toString());
        if (receivedPublicKey.equals(sharedConfig.getNodePublicKey())) {
            log.info("Received my id. It's : " + connectionReply.getNodeId());
            int receivedId = connectionReply.getNodeId();
            sharedConfig.setNodeId(receivedId);
        }
    }
}
