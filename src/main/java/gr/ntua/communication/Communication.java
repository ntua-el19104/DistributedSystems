package gr.ntua.communication;

import gr.ntua.Block;
import gr.ntua.Transaction;

import java.security.PublicKey;
import java.util.List;

public interface Communication {
    public void broadcastTranscation(Transaction transaction);

    public void broadcastAddresses();

    public int sendAddress(PublicKey pubKey);

    public List<Block> getBlockchain();

    public void broadcastBlock(Block block, int id);
}
