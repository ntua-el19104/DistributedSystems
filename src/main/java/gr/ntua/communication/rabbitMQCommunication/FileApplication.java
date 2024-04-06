package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.CliClient;
import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import gr.ntua.utils.TransactionUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FileApplication {

  private static SharedConfig sharedConfig = null;
  private static RabbitMQCommunication rabbitMQCommunication = null;

  @Autowired
  public FileApplication(SharedConfig sharedConfig, RabbitMQCommunication rabbitMQCommunication) {
    FileApplication.sharedConfig = sharedConfig;
    FileApplication.rabbitMQCommunication = rabbitMQCommunication;
  }

  public static void main(String[] args) {
    SpringApplication.run(NodeApplication.class, args);
    boolean isBootstrap = Boolean.parseBoolean(args[0]);
    int maxNetworkSize = Integer.parseInt(args[1]);
    int capacity = Integer.parseInt(args[2]);
    int stakeAmount = Integer.parseInt(args[3]);

    Node node = new Node(rabbitMQCommunication, isBootstrap, capacity);
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
      node.stake(stakeAmount);
    } catch (Exception e) {
      e.printStackTrace();
    }
    String path = "src/main/java/gr/ntua/input/trans" + node.getId() + ".txt";
    List<Transaction> list = TransactionUtils.textToTransactions(node, path);
    long startTimer = System.currentTimeMillis();
    CliClient cliClient = new CliClient(node, rabbitMQCommunication);
    for (Transaction t : list) {
      try {
        rabbitMQCommunication.broadcastTransaction(t);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try{
      Thread.sleep(120000);
    }catch (Exception e){
      e.printStackTrace();
    }
    long totalTime = node.getLastBlockTimer() - startTimer;
    System.out.println("Total Time: " + totalTime);
    cliClient.run();
  }
}
