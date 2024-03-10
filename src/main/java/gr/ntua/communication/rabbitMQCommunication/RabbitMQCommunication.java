package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.Communication;
import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
@Slf4j
public class RabbitMQCommunication implements Communication {

    private RabbitTemplate rabbitTemplate;
    private SharedConfig sharedConfig;

    @Autowired
    public RabbitMQCommunication(RabbitTemplate rabbitTemplate, SharedConfig sharedConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.sharedConfig = sharedConfig;
    }


    @Override
    public void broadcastTransaction(Transaction transaction) {

    }

    @Override
    public void broadcastAddresses() {

    }

    @Override
    public int connectToBlockchat(PublicKey pubKey) {
        sharedConfig.setNodePublicKey(pubKey);
        Node node = sharedConfig.getNode();
        if (node.isBootstrap()) {
            node.addAddress(pubKey);
            System.out.println("Bootstrap will be waiting for all nodes to connect");
            return 0;
        }
        else {
            try {
                log.info("Sending message to bootstrap to connect - through rabbitMQ");
                byte[] publicKeyBytes = pubKey.getEncoded();
                rabbitTemplate.convertAndSend(MQConfig.CONNECT_REQUEST_EXCHANGE, "", publicKeyBytes);

                //TODO add timeout so that the node does not wait forever if an error occurs
                int id = sharedConfig.getReceivedId().get();
                return id;
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Unable to set the node id. Reason: " + e.getMessage());
            }
        }
    }


    @Override
    public void broadcastBlock(Block block, int id) {
    }


}
