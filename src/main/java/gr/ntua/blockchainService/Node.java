package gr.ntua.blockchainService;

import gr.ntua.communication.Communication;
import gr.ntua.utils.TransactionUtils;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Node {

  private int nonce;
  private Wallet wallet;
  private Block block;
  private List<Block> blockchain;
  private List<NodeInfo> nodeInfoList;
  private List<PublicKey> addresses = new ArrayList<>();
  private int id;
  Communication communication;
  private List<Transaction> pending = new ArrayList<>();
  private boolean isBootstrap;
  private ReentrantLock pendingListLock;
  private ReentrantLock blockLock;
  private int blockCapacity;

  private boolean validator;

  public Node(Communication communication, boolean isBootstrap, int capacity) {
    this.communication = communication;
    this.nonce = 0;
    generateWallet();
    this.blockCapacity = capacity;
    this.block = new Block(capacity);
    this.blockchain = new ArrayList<>();
    this.id = -1;
    this.isBootstrap = isBootstrap;
    this.pendingListLock = new ReentrantLock();
    this.blockLock = new ReentrantLock();
  }

  // CORE FUNCTIONS --------------------------------------------------------------------------------
  public void generateWallet() {
    if (this.wallet == null) {
      this.wallet = new Wallet();
    }
  }

  public Transaction createTransaction(double amount, PublicKey receiverAddress, String message)
      throws Exception {
    Transaction transaction =
        new Transaction(amount, wallet.getPublicKey(), receiverAddress, nonce, message);
    transaction.setSenderId(id);
    int rid = findIdOfNode(receiverAddress);
    if (rid == -2) {
      throw new Exception("Receiver does not exist");
    }
    transaction.setReceiverId(rid);
    nonce++;
    signTransaction(transaction);
    return transaction;
  }

  public void signTransaction(Transaction transaction) {
    byte[] signature = wallet.generateSign(transaction.transactionPayloadToString().getBytes());
    transaction.setSignature(signature);
  }

  public void broadcastTransaction(Transaction transaction) {
    communication.broadcastTransaction(transaction);
  }

  public boolean verifySignature(Transaction transaction) {
    try {
      return TransactionUtils.verifySignature(
          transaction.getSenderAddress(),
          transaction.transactionPayloadToString().getBytes(),
          transaction.getSignature());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean validateTransaction(Transaction transaction) {
    return verifySignature(transaction) && verifyTransactionBalance(transaction);
  }

  private void mintBlock() {
    Block last = blockchain.get(blockchain.size() - 1);
    block.setPreviousHash(last.getCurrentHash());
    block.setIndex(last.getIndex() + 1);
    block.setTimestamp(LocalDateTime.now());
    block.setValidator(id);
    try {
      block.generateCurrentHash();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * Adds transactions to the block from the pending queue until the queue is empty or the block
   * gets filled.
   * Broadcasts the complete block. Should be repeatedly called by the validator until the block is
   * sent.
   */
  public void constructBlock() {
    int counter = 0;
    while (true) {
      pendingListLock.lock();
      try {
        while (!pending.isEmpty()) {
          Transaction current = pending.get(0);
          if (validateTransaction(current) && nodeInfoList.get(current.getSenderId())
              .addNonce(current.getNonce())) {
            updateBalance(current, id);
            counter++;
            block.addTransaction(current);
          }
          pending.remove(0);
        }
      } catch (Exception e) {
        mintBlock();
        communication.broadcastBlock(block, id);
        break;
      } finally {
        pendingListLock.unlock();
      }
    }
  }

  public boolean validateBlock(Block block) {
    if (block.getPreviousHash() == null) {
      return true;
    }
    byte[] hash = blockchain.get(blockchain.size() - 1).getCurrentHash();
    int index = blockchain.get(blockchain.size() - 1).getIndex() + 1;
    if (!Arrays.equals(block.getPreviousHash(), hash)) {
      System.out.println("First false."); //debugging line
      return false;
    } else if (block.getIndex() != index) {
      System.out.println("Second false."); //debugging line
      return false;
    }

    try {
      return getValidator(hash) == block.getValidator();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean validateChain() {
    int i = 0;
    for (Block block : blockchain) {
      if (!validateBlock(block) && i > 0) {
        return false;
      }
      i++;
    }
    return true;
  }

  public void stake(double amount) {
    try {
      Transaction temp = createTransaction(amount, null, null);
      signTransaction(temp);
      communication.broadcastTransaction(temp);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  // CORE FUNCTIONS --------------------------------------------------------------------------------

  private int findIdOfNode(PublicKey pubKey) {
    if (pubKey == null) {
      return -1;
    }
    for (int i = 0; i < addresses.size(); i++) {
      if (pubKey == addresses.get(i)) {
        return i;
      }
    }
    return -1;
  }

  public void connectToBlockchat() {
    if (id == -1) {
      id = communication.connectToBlockchat(getWallet().getPublicKey());
    }
  }

  public boolean verifyTransactionBalance(Transaction transaction) {
    int rid = transaction.getReceiverId();
    int sid = transaction.getSenderId();
    double amount = transaction.getAmount();
    if (rid == -1) {
      if (amount < 0) {
        amount *= -1;
        return amount <= nodeInfoList.get(sid).getStake();
      } else {
        return amount <= nodeInfoList.get(sid).getBalance();
      }
    }
    return (amount + transaction.getFee()) <= nodeInfoList.get(sid).getBalance();
  }

  // updates nodes info for every valid transaction and checks against replay attack(nonce)
  public void updateBalance(Transaction transaction, int validator) {
    int rid = transaction.getReceiverId();
    int sid = transaction.getSenderId();
    double amount = transaction.getAmount();
    if (sid == -1) {
      nodeInfoList.get(rid).setBalance(amount);
    } else if (rid == -1) {
      nodeInfoList.get(sid).setStake(amount);
      amount *= -1;
      nodeInfoList.get(sid).setBalance(amount);
    } else {
      nodeInfoList.get(rid).setBalance(amount);
      if (validator != -1) {
        nodeInfoList.get(validator).setBalance(transaction.getFee());
      }
      nodeInfoList.get(sid).setBalance(0 - amount - transaction.getFee());
    }
  }


  public void addPendingTransaction(Transaction transaction) {
    pendingListLock.lock();
    pending.add(transaction);
    pendingListLock.unlock();
  }

  /*
   * Should be called by all the nodes except from the validator to add the block to their
   * blockchain
   */

  public void addBlock(Block block) throws Exception {
    if (block.getValidator() == id || validateBlock(block)) {
      blockLock.lock();
      blockchain.add(block);
      blockLock.unlock();
    } else {
      throw new Exception("Node " + id + " failed to validate a block");
    }
    List<Transaction> list = block.getTransactionList();
    int validator = block.getValidator();
    if (validator != id) {
      pendingListLock.lock();
      try {
        for (Transaction i : list) {
          if (i.getSenderId() != -1) {
            if (!nodeInfoList.get(i.getSenderId()).addNonce(i.getNonce())) {
              throw new Exception("Node " + id + " found invalid nonce");
            }
          }
          updateBalance(i, validator);
          pending.removeIf(
              t -> Arrays.equals(i.getTransactionIdHash(), t.getTransactionIdHash()));
        }
      } finally {
        pendingListLock.unlock();
      }
    }
    this.validator = (id == getValidator(block.getCurrentHash()));
    System.out.println("Next validator = "+ getValidator(block.getCurrentHash()));
    this.block = new Block(blockCapacity);
    if (this.validator) {
      new Thread(this::constructBlock).start();
    }
  }

  public int getValidator(byte[] hash) throws Exception {
    int hashcode = Arrays.hashCode(hash);
    int size = nodeInfoList.size();
    double[] stakes = new double[size];
    double current = 0;
    for (int i = 0; i < size; i++) {
      current += nodeInfoList.get(i).getStake();
      stakes[i] = current;
    }
    int c = (int) current;
    if (c == 0) {
      return 0;
    }
    hashcode %= c;
    hashcode = Math.abs(hashcode);
    for (int i = 0; i < size; i++) {
      if (stakes[i] > hashcode) {
        return i;
      }
    }
    throw new Exception("did not select validator");
  }

  public Block createGenesisBlock() {
    Block block = new Block(blockCapacity);
    int size = addresses.size();
    Transaction t0 = new Transaction(size * 1000, null, addresses.get(0), 0, null);
    t0.setSenderId(-1);
    t0.setReceiverId(0);
    block.addTransactionNoCheck(t0);
    for (int i = 1; i < size; i++) {
      PublicKey publicKey = addresses.get(i);
      try {
        Transaction transaction = createTransaction(1000, publicKey, null);
        transaction.setFee(0);
        block.addTransactionNoCheck(transaction);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    block.setPreviousHash(null);
    try {
      block.generateCurrentHash();
    } catch (Exception e) {
      e.printStackTrace();
    }
    block.setValidator(-1);
    block.setIndex(0);
    return block;
  }

  public void setNodeInfo() {
    this.nodeInfoList = new ArrayList<>();
    int i = 0;
    for (PublicKey publicKey : addresses) {
      nodeInfoList.add(new NodeInfo(i, publicKey));
      i++;
    }
  }

  public void addAddress(PublicKey address) {
    addresses.add(address);
  }

  // GETTERS / SETTERS -----------------------------------------------------------------------------

  public List<Block> getBlockchain() {
    return blockchain;
  }

  public List<PublicKey> getAddresses() {
    return addresses;
  }

  public int getId() {
    return id;
  }

  public Wallet getWallet() {
    return wallet;
  }

  public void setAddresses(List<PublicKey> addresses) {
    this.addresses = addresses;
  }

  public void setBlock(Block block) {
    this.block = block;
  }

  public String viewState() {
    StringBuilder result = new StringBuilder();
    for (NodeInfo temp : nodeInfoList) {
      result.append(temp.getAddress()).append(" ").append(temp.getBalance()).append(" ")
          .append(temp.getStake()).append('\n');
    }
    return result.toString();
  }

  @Override
  public String toString() {
    return "ID: " + id + "\n" + wallet.toString() + "\n";
  }

  public String viewBlock() {
    return blockchain.get(blockchain.size() - 1).toString();
  }
}
