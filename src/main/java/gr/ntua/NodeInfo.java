package gr.ntua;

import java.security.PublicKey;

public class NodeInfo {
    private double balance = 0;

    private double temp_balance = 0;

    private double stake = 0;

    private double temp_stake = 0;

    final private int address;

    final private PublicKey publicKey;

    public NodeInfo(int addr,PublicKey pubKey){
        address = addr;
        publicKey = pubKey;
    }

    public void setBalance(double balance) {
        this.balance += balance;
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
