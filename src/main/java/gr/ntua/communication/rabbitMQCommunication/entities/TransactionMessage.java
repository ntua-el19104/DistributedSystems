package gr.ntua.communication.rabbitMQCommunication.entities;

import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.rabbitMQCommunication.utils.CommunicationUtils;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.security.PublicKey;

@NoArgsConstructor
public class TransactionMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private double amount;
    private byte[] senderAddressBytes;
    private byte[] receiverAddressBytes;
    private int senderId;
    private int receiverId;
    private String message;
    private double fee;
    private int nonce;
    private byte[] transactionIdHash;
    private byte[] signature;

    public TransactionMessage(Transaction t) {
        this.amount = t.getAmount();
        if (t.getSenderAddress() != null) this.senderAddressBytes = t.getSenderAddress().getEncoded();
        if (t.getReceiverAddress() != null) this.receiverAddressBytes = t.getReceiverAddress().getEncoded();
        this.senderId = t.getSenderId();
        this.receiverId = t.getReceiverId();
        this.message = t.getMessage();
        this.fee = t.getFee();
        this.nonce = t.getNonce();
        this.transactionIdHash = t.getTransactionIdHash();
        this.signature = t.getSignature();
    }


    public Transaction toTransaction() {
        PublicKey sender = null;
        PublicKey receiver = null;
        if (senderAddressBytes != null)
            sender = CommunicationUtils.fromBytesToPK(senderAddressBytes);
        if (receiverAddressBytes != null)
            receiver = CommunicationUtils.fromBytesToPK(receiverAddressBytes);

        return new Transaction(amount, sender, receiver,
                senderId, receiverId, message, fee, nonce, transactionIdHash, signature);
    }
}
