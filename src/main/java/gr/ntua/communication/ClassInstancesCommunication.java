package gr.ntua.communication;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class ClassInstancesCommunication implements Communication {
    private List<Node> nodesList = new ArrayList<>();

    /*
     * This function only exists in this
     * concrete implementation.
     * It should be called by this instance.
     */
    public void addNode(Node node){
        nodesList.add(node);
    }

    @Override
    public void broadcastTransaction(Transaction transaction) {
        for(Node i:nodesList){
            i.addPendingTransaction(transaction);
        }
    }

    @Override
    public void broadcastAddresses() {
        List<PublicKey> list = nodesList.get(0).getAddresses();
        for(Node i:nodesList){
            i.setAddresses(list);
        }
    }

    @Override
    public int connectToBlockchat(PublicKey pubKey) {
        Node temp = nodesList.get(0);
        int res = temp.getSize();
        temp.addAddress(pubKey);
        return res;
    }


    @Override
    public void broadcastBlock(Block block, int id) {
        for(Node i: nodesList){
            if(id != i.getId()) {
                try {
                    i.addBlock(block);
                } catch (Exception e){
                    System.out.println(e.getMessage() + "from node id " + id);
                }
            }
        }
    }


}
