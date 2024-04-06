package gr.ntua.communication;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Transaction;
import java.security.PublicKey;

public interface Communication {

  void broadcastTransaction(Transaction transaction);

  void broadcastAddresses();

  int connectToBlockchat(PublicKey pubKey);

  void broadcastBlock(Block block, int id);
}
