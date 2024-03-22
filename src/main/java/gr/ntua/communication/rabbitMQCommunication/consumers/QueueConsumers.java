package gr.ntua.communication.rabbitMQCommunication.consumers;

import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import gr.ntua.communication.rabbitMQCommunication.entities.BlockMessage;
import gr.ntua.communication.rabbitMQCommunication.entities.ConnectionReply;
import gr.ntua.communication.rabbitMQCommunication.entities.TransactionMessage;
import gr.ntua.communication.rabbitMQCommunication.utils.CommunicationUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

@Component
@Setter
@Slf4j
public class QueueConsumers {

  private RabbitTemplate rabbitTemplate;
  private SharedConfig sharedConfig;
  private int networkSize;
  private int connected;

  @Autowired
  public QueueConsumers(RabbitTemplate rabbitTemplate, SharedConfig sharedConfig) {
    this.rabbitTemplate = rabbitTemplate;
    this.sharedConfig = sharedConfig;
    this.networkSize = 1;
    this.connected = 1;
  }

  //BOOTSTRAP===============================================
  @RabbitListener(queues = "#{connectRequestQueue.name}")
  public void bootstrapListener(byte[] publicKeyBytes) {
    if (sharedConfig.getNode().isBootstrap() && networkSize < sharedConfig.getMaxNetworkSize()) {
      networkSize++;
      log.info("Bootstrap received new connect request-we have now " + networkSize
          + " connect requests:");
      PublicKey receivedPublicKey = CommunicationUtils.fromBytesToPK(publicKeyBytes);
      sharedConfig.getNode().addAddress(receivedPublicKey);
      rabbitTemplate.convertAndSend(MQConfig.CONNECT_ACCEPT_EXCHANGE, "",
          new ConnectionReply(networkSize - 1, publicKeyBytes));
      if (networkSize == sharedConfig.getMaxNetworkSize()) {
        log.info("All nodes requested connection!");
      }
    } else if (sharedConfig.getNode().isBootstrap()
        && networkSize == sharedConfig.getMaxNetworkSize()) {
      log.info("Another node attempted to connect");
    }
  }

  @RabbitListener(queues = "#{connectAcceptQueue.name}")
  public void receiveConnectionReply(ConnectionReply connectionReply) {
    //BOOTSTRAP============================
    byte[] byteArray = new byte[2];
    Arrays.fill(byteArray, (byte) 0);
    if (Arrays.equals(connectionReply.getPublicKey(), byteArray) && sharedConfig.getNode()
        .isBootstrap()) {
      connected++;
      log.info("New node has sent connection reply. Successfully connected nodes: " + connected);
      if (connected == sharedConfig.getMaxNetworkSize()) {
        sharedConfig.allNodesConnectedComplete();
        log.info("All nodes have been successfully connected!");
      }

      return;
    }
    //REGULAR NODE=========================
    if (!Arrays.equals(connectionReply.getPublicKey(), byteArray)) {
      PublicKey receivedPublicKey = CommunicationUtils.fromBytesToPK(
          connectionReply.getPublicKey());
      if (receivedPublicKey.equals(sharedConfig.getNodePublicKey())) {
        log.info("Received my id. It's : " + connectionReply.getNodeId());
        int receivedId = connectionReply.getNodeId();
        sharedConfig.setNodeId(receivedId);
        rabbitTemplate.convertAndSend(MQConfig.CONNECT_ACCEPT_EXCHANGE, "",
            new ConnectionReply(receivedId, byteArray));
      }
    }
  }

  //BOTH BOOTSTRAP AND REGULAR NODES==============================
  @RabbitListener(queues = "#{nodesAddressesQueue.name}")
  public void receiveAllNodesAddresses(byte[] publicKeyListBytes) {
    List<PublicKey> addresses = CommunicationUtils.fromBytesToPublicKeyList(publicKeyListBytes);
    sharedConfig.getNode().setAddresses(addresses);
    sharedConfig.getNode().setNodeInfo();
    log.info("I set the addresses and the nodeInfo for all nodes");
  }

  //BOTH BOOTSTRAP AND REGULAR NODES==============================
  @RabbitListener(queues = "#{blockQueue.name}")
  public void receiveBroadcastBlock(byte[] blockMessageBytes) {
    BlockMessage blockMessage = (BlockMessage) SerializationUtils.deserialize(blockMessageBytes);
    if (blockMessage.getId() != sharedConfig.getNode().getId()) {
      log.info("Received a block message from node: " + blockMessage.getId() + " with index " + blockMessage.getIndex());
      try {
        sharedConfig.getNode().addBlock(blockMessage.toBlock());
        if (blockMessage.getId() == -1) {
          sharedConfig.getReceivedGenesisBlock().complete(true);
        }
        log.info("Added block with id " + blockMessage.getId() + " to my blockchain with index " + blockMessage.getIndex());
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println(e.getMessage());
      }
    }
  }

  //BOTH BOOTSTRAP AND REGULAR NODES==============================
  @RabbitListener(queues = "#{transactionQueue.name}")
  public void receiveTransaction(byte[] transactionMessageBytes) {
    TransactionMessage transactionMessage = (TransactionMessage) SerializationUtils.deserialize(
        transactionMessageBytes);
    log.info("Received a transaction message from node: " + transactionMessage.getSenderId());
    //System.out.println("The transaction i received is: " + transactionMessage.toString());
    try {
      sharedConfig.getNode().addPendingTransaction(transactionMessage.toTransaction());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    log.info("Added received transaction to pending transactions list");

  }


}
