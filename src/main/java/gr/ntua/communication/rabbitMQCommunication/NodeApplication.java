package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.CliClient;
import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Node;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class NodeApplication {

  private static SharedConfig sharedConfig = null;
  private static RabbitMQCommunication rabbitMQCommunication = null;

  @Autowired
  public NodeApplication(SharedConfig sharedConfig, RabbitMQCommunication rabbitMQCommunication) {
    NodeApplication.sharedConfig = sharedConfig;
    NodeApplication.rabbitMQCommunication = rabbitMQCommunication;
  }


  public static void main(String[] args) {
    SpringApplication.run(NodeApplication.class, args);
    boolean isBootstrap = Boolean.parseBoolean(args[0]);
    int maxNetworkSize = Integer.parseInt(args[1]);

    Node node = new Node(rabbitMQCommunication, isBootstrap);
    sharedConfig.setNode(node);
    sharedConfig.setMaxNetworkSize(maxNetworkSize);
    node.connectToBlockchat();
    System.out.println("Node has id: " + node.getId());

    if (isBootstrap) {
      try {
        sharedConfig.getAllNodesConnected().get();
        rabbitMQCommunication.broadcastAddresses();
        Block genesis = node.createGenesisBlock();
        rabbitMQCommunication.broadcastBlock(genesis, -1);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      sharedConfig.getReceivedGenesisBlock().get();
    } catch (Exception e) {
      e.printStackTrace();
    }

    CliClient cliClient = new CliClient(node, rabbitMQCommunication);
    cliClient.run();
  }

}

