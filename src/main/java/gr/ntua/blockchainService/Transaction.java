package gr.ntua.blockchainService;

import gr.ntua.utils.TransactionUtils;

import java.security.PublicKey;
import java.util.Base64;

public class Transaction {
    private double amount;
    private PublicKey senderAddress;
    private PublicKey receiverAddress;

    private int senderId;

    private int receiverId;

    private String message;
    private double fee;
    private int nonce;
    private byte[] transactionIdHash;
    private byte[] signature;

    public Transaction(double amount, PublicKey senderAddress, PublicKey receiverAddress, int nonce,String message) {
        this.amount = amount;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.nonce = nonce;
        this.transactionIdHash = TransactionUtils.generateHash(transactionPayloadToString());
        if(amount > 0) {
            this.fee = amount * 0.03;
        } else if(message != null) {
            this.fee = message.length();
        }
        // else {} throw error invalid transaction
        if(senderAddress==null || receiverAddress==null)
            this.fee = 0;
    }


    public String transactionPayloadToString() {
        return String.format(
                "%f:%s:%s:%s:%d",
                amount,
                (message == null) ? "null" : message,
                (senderAddress == null) ? "null" : Wallet.getKeyToString(senderAddress),
                (receiverAddress == null) ? "null" : Wallet.getKeyToString(receiverAddress),
                nonce
        );
    }

    public double getAmount() {
        return amount;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PublicKey getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(PublicKey senderAddress) {
        this.senderAddress = senderAddress;
    }

    public PublicKey getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(PublicKey receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public byte[] getTransactionIdHash() {
        return transactionIdHash;
    }

    public void setTransactionIdHash(byte[] transactionIdHash) {
        this.transactionIdHash = transactionIdHash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }


    @Override
    public String toString() {
        String senderAddressToString = senderAddress!= null ? Base64.getEncoder().encodeToString(senderAddress.getEncoded()) : "null";
        String receiverAddressToString = receiverAddress!= null ? Base64.getEncoder().encodeToString(receiverAddress.getEncoded()) : "null";
        return "Transaction{" +
                "amount=" + amount +
                ", senderAddress=" + senderAddressToString +
                ", receiverAddress=" + receiverAddressToString +
                ", nonce=" + nonce +
                ", transactionIdHash=" + (transactionIdHash == null ? "null" : TransactionUtils.bytesToHex(transactionIdHash)) +
                ", signature=" + (signature == null ? "null" : TransactionUtils.bytesToHex(signature)) +
                '}';
    }
}
