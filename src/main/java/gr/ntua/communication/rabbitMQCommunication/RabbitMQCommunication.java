package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.Communication;
import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import gr.ntua.communication.rabbitMQCommunication.configurations.SharedConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RabbitMQCommunication implements Communication {

    private RabbitTemplate rabbitTemplate;
    private SharedConfig sharedConfig;
    private CompletableFuture<Integer> receivedIdFuture = new CompletableFuture<>();
    private int networkSize;


    public RabbitMQCommunication(RabbitTemplate rabbitTemplate, SharedConfig sharedConfig){
        this.rabbitTemplate = rabbitTemplate;
        this.networkSize = 0;
        this.sharedConfig = sharedConfig;
    }


    @Override
    public void broadcastTransaction(Transaction transaction) {

    }

    @Override
    public void broadcastAddresses() {

    }

    @Override
    public int connectToBlockchat(PublicKey pubKey) {
        try{
            System.out.println("Sending message");
            sharedConfig.setPublicKey(pubKey);
            String a = "thi is ";
            //conversion of public key
            rabbitTemplate.convertAndSend(MQConfig.EXCHANGE, MQConfig.CONNECTION_ROUTING_KEY, a);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            return receivedIdFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    public void broadcastBlock(Block block, int id) {
    }



}
