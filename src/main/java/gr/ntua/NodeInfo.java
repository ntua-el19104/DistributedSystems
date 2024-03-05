package gr.ntua;

import java.security.PublicKey;
import java.util.*;

public class NodeInfo {
    private double balance = 0;

    private double temp_balance = 0;

    private double stake = 0;

    private double temp_stake = 0;

    private LinkedList<Integer> nonces = new LinkedList<Integer>();

    final private int address;

    final private PublicKey publicKey;

    public NodeInfo(int addr,PublicKey pubKey){
        address = addr;
        publicKey = pubKey;
        nonces.add(-1);
    }

    public void setBalance(double balance) {
        this.balance += balance;
    }

    public boolean addNonce(int nonce){
        int largest = nonces.getLast();
        if(nonces.contains(nonce))
            return false;
        if(nonces.get(0) > nonce)
            return false;
        if(nonce == largest + 1) {
            largest++;
            nonces.removeLast();
            nonces.addLast(largest);
            return true;
        }
        if(nonce > largest + 1){
            nonces.addLast(nonce);
            return true;
        }
        nonces.add(nonce);
        Collections.sort(nonces);
        while (true){
            if(nonces.get(0).equals(nonces.get(1) - 1))
                nonces.remove(0);
            else break;
            }
        return true;
        }


    public double getBalance() {
        return balance;
    }

    public double getTempBalance() {
        return temp_balance;
    }

    public void setTempBalance(double temp_balance) {
        this.temp_balance += temp_balance;
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

    public void setTempStake(double temp_stake) {
        this.temp_stake += temp_stake;
    }

    public double getTempStake() {
        return temp_stake;
    }
}
