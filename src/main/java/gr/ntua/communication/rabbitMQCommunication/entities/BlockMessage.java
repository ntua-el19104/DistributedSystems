package gr.ntua.communication.rabbitMQCommunication.entities;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Transaction;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockMessage implements Serializable {

  private int id;
  private int capacity;
  private int index;
  private LocalDateTime timestamp;
  private Integer validator;
  private byte[] currentHash;
  private byte[] previousHash;
  private List<TransactionMessage> transactionMessageList = new ArrayList<>();

  public BlockMessage(int id, Block block) {
    this.id = id;
    this.index = block.getIndex();
    this.timestamp = block.getTimestamp();
    this.validator = block.getValidator();
    this.currentHash = block.getCurrentHash();
    this.previousHash = block.getPreviousHash();
    this.capacity = block.getCapacity();
    for (Transaction t : block.getTransactionList()) {
      this.transactionMessageList.add(new TransactionMessage(t));
    }

  }

  public Block toBlock() {
    List<Transaction> transactionList = new ArrayList<>();
    for (TransactionMessage transactionMessage : this.transactionMessageList) {
      transactionList.add(transactionMessage.toTransaction());
    }
    return new Block(this.capacity, this.index, this.timestamp, this.validator, this.currentHash,
        this.previousHash, transactionList);
  }
}
