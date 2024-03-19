package gr.ntua;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Node;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.ClassInstancesCommunication;

public class Main {
    public static void main(String[] args) {
        ClassInstancesCommunication comm = new ClassInstancesCommunication();
        Node node1 = new Node(comm,true);
        comm.addNode(node1);
        node1.connectToBlockchat();

        Node node2 = new Node(comm,false);
        comm.addNode(node2);
        node2.connectToBlockchat();
        comm.broadcastAddresses();
        node1.setNodeinfo();
        node2.setNodeinfo();
        Block genesis = node1.createGenesisBlock();
        comm.broadcastBlock(genesis,-1);

        try {
            node2.stake(100);
            for (int i = 0; i < 20; i++) {
                Transaction temp = node1.createTransaction(10, node2.getWallet().getPublicKey(), null);
                node1.signTransaction(temp);
                comm.broadcastTransaction(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        node1.setBlock(new Block());
        node1.constructBlock();
        node2.printNodes();
        node1.printNodes();
        node2.setBlock(new Block());
        node2.constructBlock();
        node2.printNodes();
        node1.printNodes();

    }
}
