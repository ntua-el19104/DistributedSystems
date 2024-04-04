package gr.ntua.blockchainService;

import java.security.PublicKey;
import java.util.*;

public class NodeInfo {
    private double balance = 0;

    private double stake = 0;

    private LinkedList<Integer> noncesList = new LinkedList<>();

    final private int address;

    final private PublicKey publicKey;

    public NodeInfo(int addr,PublicKey pubKey){
        address = addr;
        publicKey = pubKey;
        noncesList.add(-1);
    }

    public void setBalance(double balance) {
        this.balance += balance;
    }

    public boolean addNonce(int nonce){
        int largest = noncesList.getLast();
        if(noncesList.contains(nonce))
            return false;
        if(noncesList.get(0) > nonce)
            return false;
        if(nonce == largest + 1) {
            largest++;
            noncesList.removeLast();
            noncesList.addLast(largest);
            return true;
        }
        if(nonce > largest + 1){
            noncesList.addLast(nonce);
            return true;
        }
        noncesList.add(nonce);
        Collections.sort(noncesList);
        while (noncesList.size()>1){
            if(noncesList.get(0).equals(noncesList.get(1) - 1))
                noncesList.remove(0);
            else break;
            }
        return true;
        }


    public double getBalance() {
        return balance;
    }


    public int getAddress() {
        return address;
    }

    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake += stake;
    }


}
