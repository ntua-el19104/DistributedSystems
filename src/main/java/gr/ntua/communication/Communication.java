package gr.ntua.communication;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Transaction;

import java.security.PublicKey;

public interface Communication {
    public void broadcastTransaction(Transaction transaction);

    public void broadcastAddresses();

    public int connectToBlockchat(PublicKey pubKey);

    public void broadcastBlock(Block block, int id);
}
