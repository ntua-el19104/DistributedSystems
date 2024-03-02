package gr.ntua;

import gr.ntua.communication.Communication;
import gr.ntua.utils.LocalComm;
import gr.ntua.utils.TransactionUtils;

import javax.lang.model.type.NullType;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.nio.charset.StandardCharsets;


public class Node {
    private int nonce;
    private Wallet wallet;

    private Block block;

    private List<Block> blockchain;

    private List<NodeInfo> nodeinfo;

    private List<PublicKey> addresses = new ArrayList<>();

    private int id;

    LocalComm comm;

    private List<Transaction> pending;

    public Node(boolean boot, LocalComm com) {
        comm = com;
        nonce = 0;
        generateWallet();
        PublicKey pubkey = this.wallet.getPublicKey();
        block = new Block();
        blockchain = new ArrayList<>();
        comm.sendAddress(pubkey);
        comm.addNode(this);
        setId();
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

    public Transaction createTransaction(int amount, PublicKey receiverAddress,String message) throws Exception{
        Transaction transaction = new Transaction(amount, wallet.getPublicKey(), receiverAddress, nonce, message);
        transaction.setSenderId(id);
        int rid = findId(receiverAddress);
        if(rid == - 2)
            throw new Exception("Receiver does not exist");
        transaction.setReceiverId(rid);
        nonce++;
        return transaction;
    }

    private int findId(PublicKey pubKey){
        if(pubKey == null)
            return -1;
        for(int i = 0 ;i<addresses.size();i++){
            if(pubKey == addresses.get(i))
                return i;
        }
        return -2;
    }

    public void setId(){
        id = comm.sendAddress(getWallet().getPublicKey());
    }

    public void signTransaction(Transaction transaction) {
        byte[] signature = wallet.generateSign(transaction.transactionPayloadToString().getBytes());
        transaction.setSignature(signature);
    }

    public boolean verifySignature(Transaction transaction) {
        try {
            return TransactionUtils.verifySignature(transaction.getSenderAddress(), transaction.transactionPayloadToString().getBytes(), transaction.getSignature());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyTransactionBalance(Transaction transaction) {
        int rid = transaction.getReceiverId();
        int sid = transaction.getSenderId();
        double amount = transaction.getAmount();
        if(rid == -1){
            if(amount<0){
                amount *= -1;
                return amount <= nodeinfo.get(sid).getTempStake();
            } else {
                return amount <= nodeinfo.get(sid).getTempBalance();
            }
        }
        return (amount + transaction.getFee())<= nodeinfo.get(sid).getTempBalance();
    }

    public boolean validateTransaction(Transaction transaction) {
        return verifySignature(transaction) &&  verifyTransactionBalance(transaction);
    }

//    public void updateTempBalance(Transaction transaction){
//        int rid = transaction.getReceiverId();
//        int sid = transaction.getSenderId();
//        double amount = transaction.getAmount();
//        if(sid == -1){
//            nodeinfo.get(rid).setTempBalance(amount);
//        }
//        else if(rid == -1){
//            nodeinfo.get(sid).setTempStake(amount);
//            amount *= -1;
//            nodeinfo.get(sid).setTempBalance(amount);
//        } else{
//          nodeinfo.get(rid).setTempBalance(amount);
//          nodeinfo.get(sid).setTempBalance(0 - amount - transaction.getFee());
//        }
//    }


    public void updateBalance(Transaction transaction, int validator){
        int rid = transaction.getReceiverId();
        int sid = transaction.getSenderId();
        double amount = transaction.getAmount();
        if(sid == -1){
            nodeinfo.get(rid).setBalance(amount);
        }
        else if(rid == -1){

            nodeinfo.get(sid).setStake(amount);
            amount *= -1;
            nodeinfo.get(sid).setBalance(amount);
        } else{
            nodeinfo.get(rid).setBalance(amount);
            nodeinfo.get(validator).setBalance(transaction.getFee());
            nodeinfo.get(sid).setBalance(0 - amount - transaction.getFee());
        }
    }

    public void addTransactionToBlock(Transaction transaction) {
        try{
            block.addTransaction(transaction);
        } catch (Exception e) {
            //copy current block and pass to new thread to mine the new block
            mintBlock();
            comm.broadcastBlock(block,id);
            block = new Block();
            try{
                block.addTransaction(transaction);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addPendingTransaction(Transaction transaction){
        pending.add(transaction);
    }

    public Block getBlock() {
        return block;
    }

    public void addBlock(Block block) throws Exception{
        if(validateBlock(block))
            blockchain.add(block);
        else throw new Exception("Node " + id + "failed to validate a block");
        List<Transaction> list = block.getTransactionList();
        int validator = findId(block.getValidator());
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public int getSize(){
        return addresses.size();
    }

    public List<PublicKey> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PublicKey> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(PublicKey address){
        addresses.add(address);
    }

    public int getId() {
        return id;
    }

    public int getValidator(byte[] hash) throws Exception{
        int hashcode = Arrays.hashCode(hash);
        int size = nodeinfo.size();
        double[] stakes = new double[size];
        double current = 0;
        for (int i = 0; i<size; i++){
            current += nodeinfo.get(i).getStake();
            stakes[i] = current;
        }
        int c = (int)current;
        hashcode%=c;
        for(int i=-0; i<size;i++){
            if(stakes[i]>hashcode)
                return i;
        }
        throw new Exception("did not select validator");
    }

    private void mintBlock(){
        Block last = blockchain.get(blockchain.size() - 1);
        block.setPreviousHash(last.getCurrentHash());
        block.setIndex(last.getIndex() + 1);
        block.setTimestamp(LocalDateTime.now());
        block.setValidator(getWallet().getPublicKey());
        try{
            block.generateCurrentHash();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean validateBlock(Block block){
        byte[] hash = blockchain.get(blockchain.size() - 1).getCurrentHash();
        if(block.getPreviousHash()!=hash){
            return false;
        }
        try{
            return addresses.get(getValidator(hash)) == block.getValidator();
        } catch (Exception e){
            return false;
        }
    }

    public boolean validateChain(){
        int i = 0;
        for(Block block: blockchain){
            if(!validateBlock(block) && i>0)
                return false;
            i++;
        }
        return true;
    }

    public void setBlockchain(List<Block> blockchain) {
        this.blockchain = blockchain;
    }

    public void printNodes(){
        for (NodeInfo temp : nodeinfo) {
            System.out.println(temp.getAddress() + " " + temp.getBalance() + " " + temp.getTempBalance());
        }
    }

    public void setNodeinfo() {
        this.nodeinfo = new ArrayList<>();
        int i = 0;
        for(PublicKey publicKey: addresses){
            nodeinfo.add(new NodeInfo(i,publicKey));
            i++;
        }
    }
}
