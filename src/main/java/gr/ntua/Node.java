package gr.ntua;

import gr.ntua.utils.TransactionUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private int nonce;
    private Wallet wallet;

    private Block block;

    private List<Block> blockchain;

    public Node() {
        this.nonce = 0;
        block = new Block();
        blockchain = new ArrayList<>();
    }


    public int getNonce() {
        return nonce;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void generateWallet() {
        if(this.wallet == null) {
            this.wallet = new Wallet();
        }
    }

    public Transaction createTransaction(int amount, PublicKey receiverAddress) {
        Transaction transaction = new Transaction(amount, wallet.getPublicKey(), receiverAddress, nonce);
        nonce++;
        return transaction;
    }

    public void signTransaction(Transaction transaction) {
        byte[] signature = wallet.generateSign(transaction.transactionPayloadToString().getBytes());
        transaction.setSignature(signature);
    }

    public void broadcastTransaction(Transaction transaction) {
        //TODO Implement
    }

    public boolean verifySignature(Transaction transaction) {
        try {
            return TransactionUtils.verifySignature(transaction.getSenderAddress(), transaction.transactionPayloadToString().getBytes(), transaction.getSignature());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyTransactionBalance(Transaction transaction) {
        //TODO Implement
        return true;
    }

    public boolean verifyTransaction(Transaction transaction) {
        return verifySignature(transaction) &&  verifyTransactionBalance(transaction);
    }

    public void addTransactionToBlock(Transaction transaction) {
        try{
            block.addTransaction(transaction);
        } catch (Exception e) {
            //copy current block and pass to new thread to mine the new block
            block = new Block();
            try {
                block.addTransaction(transaction);
            } catch (Exception ex) {
            }
        }
    }

    public Block getBlock() {
        return block;
    }



}
