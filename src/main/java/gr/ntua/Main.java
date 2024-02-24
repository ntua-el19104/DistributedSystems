package gr.ntua;

import gr.ntua.utils.TransactionUtils;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args){
        Node node1 = new Node();
        node1.generateWallet();
        Node node2 = new Node();
        node2.generateWallet();

        Transaction t1 = node1.createTransaction(1, node2.getWallet().getPublicKey());
        System.out.println(t1);
        node1.signTransaction(t1);
        System.out.println(t1);
        //communication
        if(node2.verifySignature(t1)){
            System.out.println("Verify");
            node2.addTransactionToBlock(t1);
        }
        System.out.println(node2.getBlock());

    }
}
