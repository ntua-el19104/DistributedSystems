package gr.ntua;

import gr.ntua.communication.Communication;
import gr.ntua.utils.LocalComm;
import gr.ntua.utils.TransactionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        LocalComm comm = new LocalComm();
        Node node1 = new Node(true, comm);
        Node node2 = new Node(false, comm);
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
                comm.broadcastTranscation(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        node1.setBlock(new Block());
        node1.addTransactionsToBlock();
        node2.printNodes();
        node1.printNodes();
        node2.setBlock(new Block());
        node2.addTransactionsToBlock();
        node2.printNodes();
        node1.printNodes();
    }
}
