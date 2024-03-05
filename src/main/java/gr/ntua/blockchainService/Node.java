package gr.ntua.blockchainService;

import gr.ntua.communication.ClassInstancesCommunication;
import gr.ntua.communication.Communication;
import gr.ntua.utils.TransactionUtils;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {
    private int nonce;
    private Wallet wallet;

    private Block block;

    private List<Block> blockchain;

    private List<NodeInfo> nodeInfoList;

    private List<PublicKey> addresses = new ArrayList<>();

    private int id;

    Communication communicationInterface;

    private List<Transaction> pending = new ArrayList<>();

    public Node(Communication communicationInterface) {
        this.communicationInterface = communicationInterface;
        nonce = 0;
        generateWallet();
        block = new Block();
        blockchain = new ArrayList<>();
        id = -1;
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

    public Transaction createTransaction(double amount, PublicKey receiverAddress,String message) throws Exception{
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

    public void connectToBlockchat(){
        if(id == -1){
            id = communicationInterface.connectToBlockchat(getWallet().getPublicKey());
        }
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
        return amount <= nodeInfoList.get(sid).getTempStake();
            } else {
        return amount <= nodeInfoList.get(sid).getBalance();
            }
        }
    return (amount + transaction.getFee()) <= nodeInfoList.get(sid).getBalance();
    }

    public boolean validateTransaction(Transaction transaction) {
        return verifySignature(transaction) &&  verifyTransactionBalance(transaction);
    }


    //updates nodes info for every valid transaction and checks against replay attack(nonce)
    public void updateBalance(Transaction transaction, int validator) throws Exception{
        int rid = transaction.getReceiverId();
        int sid = transaction.getSenderId();
        double amount = transaction.getAmount();
        int nonce = transaction.getNonce();
        if(sid == -1){
      nodeInfoList.get(rid).setBalance(amount);
        }
        else if(rid == -1){
      nodeInfoList.get(sid).setStake(amount);
            amount *= -1;
      nodeInfoList.get(sid).setBalance(amount);
      if (!nodeInfoList.get(sid).addNonce(nonce)) throw new Exception("Invalid nonce");
        } else{
      nodeInfoList.get(rid).setBalance(amount);
      nodeInfoList.get(validator).setBalance(transaction.getFee());
      nodeInfoList.get(sid).setBalance(0 - amount - transaction.getFee());
      if (!nodeInfoList.get(sid).addNonce(nonce)) throw new Exception("Invalid nonce");
        }

    }

  // adds transactions to the block from the pending queue until the queue is empty or the block
  // gets filled.
  // Broadcasts the complete block. Should be repeatedly called by the validator until the block is
  // sent.
  public void addTransactionsToBlock() {
        int counter = 0;
        while(!pending.isEmpty()){
            Transaction current = pending.get(0);
            if(validateTransaction(current)){
                try {
                    block.addTransaction(current);
                    updateBalance(current,id);
                    counter++;
                    pending.remove(0);
                } catch (Exception e){
                    mintBlock();
                    blockchain.add(block);
                    communicationInterface.broadcastBlock(block,id);
                    break;
                }
            }
        }
        System.out.println(counter + " Transactions were added");
    }

    public void addPendingTransaction(Transaction transaction){
        pending.add(transaction);
    }

    public Block getBlock() {
        return block;
    }

    //Should be called by all the nodes except from the validator to add the block to their blockchain
    public void addBlock(Block block) throws Exception{
        if(validateBlock(block)) {
            blockchain.add(block);
        }
        else {
            throw new Exception("Node " + id + " failed to validate a block");
        }
        List<Transaction> list = block.getTransactionList();
        int validator = block.getValidator();
        for(Transaction i:list){
            updateBalance(i,validator);
            pending.remove(i);
        }
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void stake(double amount){
        try{
            Transaction temp = createTransaction(amount,null,null);
            signTransaction(temp);
      communicationInterface.broadcastTransaction(temp);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Block createGenesisBlock(){
        Block block = new Block();
        int size = addresses.size();
        Transaction t0 = new Transaction(size*1000,null, addresses.get(0),0,null );
        t0.setSenderId(-1);
        t0.setReceiverId(0);
        block.addTransactionNoCheck(t0);
    communicationInterface.broadcastTransaction(t0);
        for (int i = 1; i < size; i++) {
            PublicKey publicKey = addresses.get(i);
            try{
                Transaction transaction = createTransaction(1000,publicKey,null);
                transaction.setFee(0);
                block.addTransactionNoCheck(transaction);
        communicationInterface.broadcastTransaction(transaction);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        block.setPreviousHash(null);
        try{
            block.generateCurrentHash();
        } catch (Exception e){
            e.printStackTrace();
        }
        block.setValidator(0);
        block.setIndex(0);
        return block;
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
    int size = nodeInfoList.size();
        double[] stakes = new double[size];
        double current = 0;
        for (int i = 0; i<size; i++){
      current += nodeInfoList.get(i).getStake();
            stakes[i] = current;
        }
        int c = (int)current;
        if(c==0){
            return 0;
        }
        hashcode%=c;
        hashcode = Math.abs(hashcode);
        for(int i=0; i<size;i++){
            if(stakes[i]>hashcode) {
                return i;
            }
        }
        throw new Exception("did not select validator");
    }

    private void mintBlock(){
        Block last = blockchain.get(blockchain.size() - 1);
        block.setPreviousHash(last.getCurrentHash());
        block.setIndex(last.getIndex() + 1);
        block.setTimestamp(LocalDateTime.now());
        block.setValidator(id);
        block.setIndex(last.getIndex() + 1);
        try{
            block.generateCurrentHash();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean validateBlock(Block block){
        if(block.getPreviousHash()==null) {
            return true;
        }
        byte[] hash = blockchain.get(blockchain.size() - 1).getCurrentHash();
        int index =  blockchain.get(blockchain.size() - 1).getIndex() + 1;
        if(block.getPreviousHash()!=hash || block.getIndex()!=index){
            return false;
        }
        try{
            return getValidator(hash) == block.getValidator();
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
    for (NodeInfo temp : nodeInfoList) {
            System.out.println(temp.getAddress() + " " + temp.getBalance() + " " + temp.getTempBalance());
        }
    }

    public void setNodeinfo() {
    this.nodeInfoList = new ArrayList<>();
        int i = 0;
        for(PublicKey publicKey: addresses){
      nodeInfoList.add(new NodeInfo(i, publicKey));
            i++;
        }
    }
}