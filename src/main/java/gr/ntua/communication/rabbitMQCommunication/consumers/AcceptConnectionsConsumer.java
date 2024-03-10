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
        this.networkSize = 1;
    }

    @RabbitListener(queues = "#{connectRequestQueue.name}")
    public void bootstrapListener(byte[] publicKeyBytes) {
        if (sharedConfig.getNode().isBootstrap() && networkSize < sharedConfig.getMaxNetworkSize()) {
            networkSize++;
            log.info("Bootstrap received another node request-the network size now is:" + networkSize);
            PublicKey receivedPublicKey = CommunicationUtils.fromBytesToPK(publicKeyBytes);
            sharedConfig.getNode().addAddress(receivedPublicKey);
            rabbitTemplate.convertAndSend(MQConfig.CONNECT_ACCEPT_EXCHANGE, "", new ConnectionReply(networkSize - 1, publicKeyBytes));
            if (networkSize == sharedConfig.getMaxNetworkSize()) {
                log.info("All nodes connected!");
                sharedConfig.allNodesConnectedComplete();
            }
        }else if(sharedConfig.getNode().isBootstrap() && networkSize == sharedConfig.getMaxNetworkSize()){
            log.info("Another node attempted to connect");
        }
    }

    @RabbitListener(queues = "#{connectAcceptQueue.name}")
    public void receiveConnectionReply(ConnectionReply connectionReply) {
        PublicKey receivedPublicKey = CommunicationUtils.fromBytesToPK(connectionReply.getPublicKey());
        if (receivedPublicKey.equals(sharedConfig.getNodePublicKey())) {
            log.info("Received my id. It's : " + connectionReply.getNodeId());
            int receivedId = connectionReply.getNodeId();
            sharedConfig.setNodeId(receivedId);
        }
    }
}
