package gr.ntua.blockchainService;

import gr.ntua.utils.TransactionUtils;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Block {

  private int capacity;
  private int index;
  private LocalDateTime timestamp;
  private Integer validator;
  private byte[] currentHash;
  private byte[] previousHash;
  private List<Transaction> transactionList = new ArrayList<>();

  public Block(int capacity) {
    this.capacity = capacity;
  }

  public void generateCurrentHash() throws Exception {
    if (transactionList.size() < capacity && previousHash != null) {
      throw new Exception("Number of Transactions lees than 10.");
    }
    currentHash = TransactionUtils.generateHash(blockPayloadToString());
  }

  public void addTransaction(Transaction transaction) throws Exception {
    if (transactionList.size() < capacity - 1) {
      transactionList.add(transaction);
    } else if (transactionList.size() == capacity - 1) {
      transactionList.add(transaction);
      throw new Exception("Block filled with transactions");
    } else {
      throw new Exception("Block filled with transactions");
    }
  }

  public void addTransactionNoCheck(Transaction transaction) {
    transactionList.add(transaction);
  }

  private String transactionsPayloadToString() {
    return transactionList.stream()
        .map(Transaction::transactionPayloadToString)
        .collect(Collectors.joining("\n"));
  }

  private String blockPayloadToString() {
    if (previousHash == null) {
      return "null";
    }
    return index + new String(previousHash, StandardCharsets.UTF_8) +
        timestamp.toString() + transactionsPayloadToString();
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getCapacity() {
    return capacity;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public int getValidator() {
    return validator;
  }

  public void setValidator(int validator) {
    this.validator = validator;
  }

  public byte[] getCurrentHash() {
    return currentHash;
  }

  public byte[] getPreviousHash() {
    return previousHash;
  }

  public void setPreviousHash(byte[] previousHash) {
    this.previousHash = previousHash;
  }

  public List<Transaction> getTransactionList() {
    return transactionList;
  }


  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("Block: \n" +
        "capacity: " + capacity + "\n" +
        "index: " + index + "\n" +
        "timestamp: " + timestamp + "\n" +
        "validator: " + (validator == null ? "null" : validator.toString()) + "\n" +
        "currentHash: " + (currentHash == null ? "null" : TransactionUtils.bytesToHex(currentHash))
        + "\n" +
        "previousHash: " + (previousHash == null ? "null"
        : TransactionUtils.bytesToHex(previousHash)) + "\n" +
        "transactionList: [");

    if (!transactionList.isEmpty()) {
      result.append("\n");
      result.append("1. ").append(transactionList.get(0).toString());
      for (int i = 1; i < transactionList.size(); i++) {
        result.append(",\n");
        result.append(i + 1).append(". ").append(transactionList.get(i).toString());
      }
    }

    result.append("]");
    return result.toString();
  }
}