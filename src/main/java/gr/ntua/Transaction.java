package gr.ntua;

import gr.ntua.utils.TransactionUtils;

import java.security.*;
import java.util.Base64;

public class Transaction {
    private int amount;
    private PublicKey senderAddress;
    private PublicKey receiverAddress;
    private int nonce;
    private byte[] transactionIdHash;
    private byte[] signature;

    public Transaction(int amount, PublicKey senderAddress, PublicKey receiverAddress, int nonce) {
        this.amount = amount;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.nonce = nonce;
        this.transactionIdHash = TransactionUtils.generateHash(transactionPayloadToString());
    }


    public String transactionPayloadToString() {
        return String.format("%d:%s:%s:%d", amount, senderAddress.toString(), receiverAddress.toString(), nonce);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
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
                ", transactionIdHash=" + (transactionIdHash == null ? "null" : bytesToHex(transactionIdHash)) +
                ", signature=" + (signature == null ? "null" : bytesToHex(signature)) +
                '}';
    }
}
