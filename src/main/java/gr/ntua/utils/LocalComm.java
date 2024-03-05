package gr.ntua.utils;

import gr.ntua.Block;
import gr.ntua.Node;
import gr.ntua.Transaction;
import gr.ntua.communication.Communication;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class LocalComm implements Communication {
    private List<Node> Nodes = new ArrayList<>();
    public void addNode(Node node){
        Nodes.add(node);
    }


    @Override
    public void broadcastTranscation(Transaction transaction) {
        for(Node i:Nodes){
            i.addPendingTransaction(transaction);
        }
    }

    @Override
    public void broadcastAddresses() {
        List<PublicKey> list = Nodes.get(0).getAddresses();
        for(Node i:Nodes){
            i.setAddresses(list);
        }
    }

    @Override
    public int sendAddress(PublicKey pubKey) {
        Node temp = Nodes.get(0);
        int res = temp.getSize();
        temp.addAddress(pubKey);
        return res;
    }


    @Override
    public void broadcastBlock(Block block, int id) {
        for(Node i:Nodes){
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
