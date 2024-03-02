package gr.ntua;

import gr.ntua.communication.Communication;
import gr.ntua.utils.LocalComm;
import gr.ntua.utils.TransactionUtils;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        LocalComm comm = new LocalComm();
        Node node1 = new Node(true, comm);
        node1.generateWallet();
        Node node2 = new Node(false, comm);
        node2.generateWallet();
        comm.addNode(node1);
        comm.addNode(node2);
        node1.setId();
        node2.setId();
        comm.broadcastAddresses();
        node1.setNodeinfo();
        node2.setNodeinfo();

        Transaction t0 = new Transaction(1000, null, node1.getWallet().getPublicKey(), 0, null);
        t0.setReceiverId(0);
        t0.setSenderId(-1);
        node1.updateBalance(t0,0);
        node2.updateBalance(t0,0);
        Transaction[] t = new Transaction[10];
        try {
            for (int i = 0; i < 9; i++) {
                t[i] = node1.createTransaction(1, node2.getWallet().getPublicKey(), null);
                node1.signTransaction(t[i]);
                //comm.broadcastTranscation(t[0]);
                if(node1.validateTransaction(t[i]))
                    node1.updateBalance(t[i],0);
                if(node2.validateTransaction(t[i]))
                    node2.updateBalance(t[i],0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        node1.printNodes();
        node2.printNodes();

    }
}
