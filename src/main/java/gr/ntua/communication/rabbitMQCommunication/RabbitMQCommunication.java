package gr.ntua.communication.rabbitMQCommunication;

import gr.ntua.blockchainService.Block;
import gr.ntua.blockchainService.Transaction;
import gr.ntua.communication.Communication;
import gr.ntua.communication.rabbitMQCommunication.configurations.MQConfig;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.security.PublicKey;

@NoArgsConstructor
public class RabbitMQCommunication implements Communication {

    private RabbitTemplate rabbitTemplate;

    public RabbitMQCommunication(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
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
            String p = pubKey.toString();
            rabbitTemplate.convertAndSend(MQConfig.EXCHANGE,MQConfig.ROUTING_KEY, p);
        }
        catch (Exception e){
            e.printStackTrace();
        }
       return -1;
    }


    @Override
    public void broadcastBlock(Block block, int id) {

    }
}
