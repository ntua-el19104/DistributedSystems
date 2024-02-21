package gr.ntua;

import gr.ntua.utils.TransactionUtils;

import java.security.PublicKey;

public class Node {
    private int nonce;
    private Wallet wallet;

    public Node(){
        this.nonce = 0;
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



}
