package gr.ntua.utils;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Transaction;


import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class ViewBlockchain {

    private class Node{
        double balance = 0;
        double id;
        double stake = 0;
        int validations = 0;

        void increaseValidations(){
            validations++;
        }

        void updateStake(double amount){
            stake += amount;
        }

        void updateBalance(double amount){
            balance += amount;
        }

        Node(int id){
            this.id = id;
        }
    }
    private List<Block> blockchain;
    private int networkSize;

    private List<Node> info;

    public ViewBlockchain(List<Block> blockchain, int networkSize){
        this.blockchain = blockchain;
        this.networkSize = networkSize;
        setInfo();
    }

    public String viewInfo(){
        StringBuilder res = new StringBuilder();
        for(Node n: info){
            res.append("id ").append(n.id).append(" balance ").append(n.balance).append(" stake ").append(n.stake).append(" validations ").append(n.validations).append("\n");
        }
        return res.toString();
    }

    private void setInfo() {
        this.info = new ArrayList<>();
        for (int i = 0; i < networkSize; i++) {
            info.add(new Node(i));
        }
        for(Block b: blockchain){
            int validator = b.getValidator();
            if(validator!=-1){
                info.get(validator).increaseValidations();
            }
            List<Transaction> list = b.getTransactionList();
            for(Transaction t:list){
                int sid = t.getSenderId();
                int rid = t.getReceiverId();
                double amount = t.getAmount();
                //System.out.println(t.getFee() + " " + sid + " " + rid);
                if(sid==-1){
                    info.get(rid).updateBalance(amount);
                }else if(rid==-1){
                    info.get(sid).updateStake(amount);
                    amount *= -1;
                    info.get(sid).updateBalance(amount);
                }else{
                    info.get(rid).updateBalance(amount);
                    amount*=-1;
                    info.get(sid).updateBalance(amount - t.getFee());
                    if(validator!=-1)
                        info.get(validator).updateBalance(t.getFee());
                }
            }
        }
    }



    public String viewMessages(){
        StringBuilder res = new StringBuilder();
        for(Block b: blockchain){
            int validator = b.getValidator();
            if(validator!=-1){
                info.get(validator).increaseValidations();
            }
            List<Transaction> list = b.getTransactionList();
            for(Transaction t:list){
                int sid = t.getSenderId();
                int rid = t.getReceiverId();
                String message = t.getMessage();
                if(message!=null){
                    res.append("Node ").append(sid).append("send ").append(message).append(" to node ").append(rid).append("\n");
                }
            }
        }
        return res.toString();
    }

    public String viewConversation(int n1, int n2){
        StringBuilder res = new StringBuilder();
        for(Block b: blockchain){
            int validator = b.getValidator();
            if(validator!=-1){
                info.get(validator).increaseValidations();
            }
            List<Transaction> list = b.getTransactionList();
            for(Transaction t:list){
                int sid = t.getSenderId();
                int rid = t.getReceiverId();
                String message = t.getMessage();
                if(message!=null &&(sid==n1 || rid== n1) &&(sid==n2 || rid==n2)){
                    res.append("Node ").append(sid).append("send ").append(message).append("\n");
                }
            }
        }
        return res.toString();
    }
}
