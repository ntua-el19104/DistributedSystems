package gr.ntua;

import gr.ntua.utils.TransactionUtils;

import java.security.*;
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
        this.fee = amount*0.03;
        if(message != null){
            this.fee += message.length();
        }
    }


    public String transactionPayloadToString() {
        return String.format("%f:%s:%s:%d", amount, (senderAddress == null) ? "null" : senderAddress.toString(), (receiverAddress == null) ? "null" :receiverAddress.toString(), nonce);
    }

    public double getAmount() {
        return amount;
    }

    public double getFee() {
        return fee;
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
        String senderAddressToString = Base64.getEncoder().encodeToString(senderAddress.getEncoded());
        String receiverAddressToString = Base64.getEncoder().encodeToString(receiverAddress.getEncoded());
        return "Transaction{" +
                "amount=" + amount +
                ", senderAddress=" + (senderAddressToString == null ? "null" : senderAddressToString.substring(0,10)) +
                ", receiverAddress=" + (receiverAddressToString == null ? "null" : receiverAddressToString.substring(0,10)) +
                ", nonce=" + nonce +
                ", transactionIdHash=" + (transactionIdHash == null ? "null" : TransactionUtils.bytesToHex(transactionIdHash)) +
                ", signature=" + (signature == null ? "null" : TransactionUtils.bytesToHex(signature)) +
                '}';
    }
}
