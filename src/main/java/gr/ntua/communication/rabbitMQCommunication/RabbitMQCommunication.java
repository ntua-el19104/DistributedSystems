package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.Communication;
import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import gr.ntua.communication.rabbitMQCommunication.entities.BlockMessage;
import gr.ntua.communication.rabbitMQCommunication.entities.TransactionMessage;
import gr.ntua.communication.rabbitMQCommunication.utils.CommunicationUtils;
import java.security.PublicKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQCommunication implements Communication {

  private final RabbitTemplate rabbitTemplate;
  private final SharedConfig sharedConfig;

  @Autowired
  public RabbitMQCommunication(RabbitTemplate rabbitTemplate, SharedConfig sharedConfig) {
    this.rabbitTemplate = rabbitTemplate;
    this.sharedConfig = sharedConfig;
  }

  @Override
  public void broadcastTransaction(Transaction transaction) {
    try {
      TransactionMessage transactionMessage = new TransactionMessage(transaction);
      byte[] toSend = SerializationUtils.serialize(transactionMessage);
      rabbitTemplate.convertAndSend(MQConfig.TRANSACTION_EXCHANGE, "", toSend);
      log.info("I have sent a transaction to all nodes - broadcastTransaction");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void broadcastAddresses() {
    byte[] publicKeyListBytes = CommunicationUtils.fromPublicKeyListToBytes(
        sharedConfig.getNode().getAddresses());
    rabbitTemplate.convertAndSend(MQConfig.NODES_ADDRESSES_EXCHANGE, "", publicKeyListBytes);
    log.info("I sent the addresses to all nodes");
  }

  @Override
  public int connectToBlockchat(PublicKey pubKey) {
    sharedConfig.setNodePublicKey(pubKey);
    Node node = sharedConfig.getNode();
    if (node.isBootstrap()) {
      node.addAddress(pubKey);
      System.out.println("Bootstrap will be waiting for all nodes to connect");
      return 0;
    } else {
      try {
        log.info("Sending message to bootstrap to connect - through rabbitMQ");
        byte[] publicKeyBytes = pubKey.getEncoded();
        rabbitTemplate.convertAndSend(MQConfig.CONNECT_REQUEST_EXCHANGE, "", publicKeyBytes);
        return sharedConfig.getReceivedId().get();
      } catch (Exception e) {
        e.printStackTrace();
        throw new Error("Unable to set the node id. Reason: " + e.getMessage());
      }
    }
  }

  @Override
  public void broadcastBlock(Block block, int id) {
    try {
      BlockMessage blockMessage = new BlockMessage(id, block);
      byte[] toSend = SerializationUtils.serialize(blockMessage);
      rabbitTemplate.convertAndSend(MQConfig.BLOCK_EXCHANGE, "", toSend);
      log.info("I have sent a block to all nodes with index " + blockMessage.getIndex()
          + " - broadcastBlock");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
