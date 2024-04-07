package gr.ntua.api;

import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.Communication;
import gr.ntua.utils.ViewBlockchain;
import java.security.PublicKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockchatController {

  private final Node node;
  private final Communication communication;

  public BlockchatController(Node node, Communication communication) {
    this.node = node;
    this.communication = communication;
  }


  @PostMapping("/transaction")
  public void createTransaction(
      @RequestParam("recipientAddress") int recipientAddress,
      @RequestParam("amount") int amount,
      @RequestParam(value = "message", required = false) String message) throws Exception {
    PublicKey receiver = node.getAddresses().get(recipientAddress);
    Transaction t = message != null ? node.createTransaction(amount, receiver, message)
        : node.createTransaction(amount, receiver, null);
    communication.broadcastTransaction(t);
  }

  @PostMapping("/stake")
  public void stakeCoins(@RequestParam("amount") int amount) {
    node.stake(amount);
  }

  @GetMapping("/view")
  public String viewLastBlock() {
    return node.viewBlock();
  }

  @GetMapping("/balance")
  public double getBalance() {
    return node.getNodeInfoList().get(node.getId()).getBalance();
  }

  @GetMapping("/state")
  public String viewNodeState() {
    return node.viewState();
  }

  @GetMapping("/blockchain")
  public String viewBlockchain() {
    ViewBlockchain view = new ViewBlockchain(node.getBlockchain(), node.getAddresses().size());
    return view.viewInfo();
  }

  @GetMapping("/messages")
  public String viewMessages() {
    ViewBlockchain viewBlockchain = new ViewBlockchain(node.getBlockchain(),
        node.getAddresses().size());
    return viewBlockchain.viewMessages();
  }
}

