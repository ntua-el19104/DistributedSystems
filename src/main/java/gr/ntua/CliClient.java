package gr.ntua;

import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.Communication;
import gr.ntua.utils.ViewBlockchain;
import java.security.PublicKey;
import java.util.Scanner;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CliClient implements Runnable {

  private Node node;
  private Communication communication;

  public void printHelp() {
    System.out.println("Available commands:\n");
    System.out.println("t <recipient_address> <amount> - New transaction");
    System.out.println("m <recipient_address> <message> - New transaction");
    System.out.println("stake <amount> - Set the node stake");
    System.out.println("view - View last block");
    System.out.println("balance - View your balance");
    System.out.println("exit - Exit BlockChat");
    System.out.println("state - View information about the nodes");
    System.out.println("blockchain - View blockchain information(based on last validated block)");
    System.out.println("messages - View all exchanged messages");
  }

  public void processCommand(String command) throws Exception {
    String[] parts = command.split(" ");
    String action = parts[0].toLowerCase();
    switch (action) {
      case "t":
        if (parts.length == 3) {
          PublicKey receiver = node.getAddresses().get(Integer.parseInt(parts[1]));
          Transaction t = node.createTransaction(Integer.parseInt(parts[2]), receiver, null);
          communication.broadcastTransaction(t);
        } else {
          System.out.println("Invalid command. Usage: t <recipient_address> <message>");
        }
        break;
      case "m":
        if (parts.length == 3) {
          PublicKey receiver = node.getAddresses().get(Integer.parseInt(parts[1]));
          Transaction t = node.createTransaction(0, receiver, parts[2]);
          communication.broadcastTransaction(t);
        } else {
          System.out.println("Invalid command. Usage: t <recipient_address> <message>");
        }
        break;
      case "stake":
        if (parts.length == 2) {
          System.out.println("You staked " + parts[1] + " coins.");
          node.stake(Integer.parseInt(parts[1]));
        } else {
          System.out.println("Invalid command. Usage: stake <amount>");
        }
        break;
      case "view":
        String lastBlock = node.viewBlock();
        System.out.println(lastBlock);
        break;
      case "balance":
        System.out.println(
            "Your balance is: " + node.getNodeInfoList().get(node.getId()).getBalance());
        break;
      case "help":
        printHelp();
        break;
      case "state":
        String state = node.viewState();
        System.out.println(state);
        break;
      case "blockchain":
        ViewBlockchain view = new ViewBlockchain(node.getBlockchain(), node.getAddresses().size());
        String info = view.viewInfo();
        System.out.println(info);
        break;
      case "messages":
        ViewBlockchain viewBlockchain = new ViewBlockchain(node.getBlockchain(),
            node.getAddresses().size());
        String messages = viewBlockchain.viewMessages();
        System.out.println(messages);
        break;
      default:
        System.out.println("Unknown command. Type 'help' for instructions.\n");
        break;
    }
  }

  @Override
  public void run() {
    Scanner scanner = new Scanner(System.in);
    String command;

    do {
      System.out.print("Enter command (type 'help' for instructions):\n");
      command = scanner.nextLine().trim();
      try {
        processCommand(command);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } while (!command.equalsIgnoreCase("exit"));

    scanner.close();
    System.out.println("Exiting BlockChat.");
  }

}

