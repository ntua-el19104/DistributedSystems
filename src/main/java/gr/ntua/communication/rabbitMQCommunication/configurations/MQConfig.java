package gr.ntua.communication.rabbitMQCommunication.configurations;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String CONNECT_REQUEST_EXCHANGE = "connect_request_exchange";
    public static final String CONNECT_ACCEPT_EXCHANGE = "connect_accept_exchange";
    public static final String NODES_ADDRESSES_EXCHANGE = "nodes_addresses_exchange";
    public static final String BLOCK_EXCHANGE = "block_exchange";
    public static final String TRANSACTION_EXCHANGE = "transaction_exchange";

    //REGULAR NODE =====================================================================
    @Bean
    public FanoutExchange connectRequestExchange() {
        return new FanoutExchange(CONNECT_REQUEST_EXCHANGE);
    }

    @Bean
    public Queue connectRequestQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding connectRequestBinding(Queue connectRequestQueue, FanoutExchange connectRequestExchange) {
        return BindingBuilder.bind(connectRequestQueue).to(connectRequestExchange);
    }


    //BOOTSTRAP NODE =====================================================================
    @Bean
    public FanoutExchange connectAcceptExchange() {
        return new FanoutExchange(CONNECT_ACCEPT_EXCHANGE);
    }

    @Bean
    public Queue connectAcceptQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding connectAcceptBinding(Queue connectAcceptQueue, FanoutExchange connectAcceptExchange) {
        return BindingBuilder.bind(connectAcceptQueue).to(connectAcceptExchange);
    }

    //FOR ANY TYPE OF NODE ===============================================================
    @Bean
    public FanoutExchange nodesAddressesExchange() {
        return new FanoutExchange(NODES_ADDRESSES_EXCHANGE);
    }

    @Bean
    public Queue nodesAddressesQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding nodesAddressesBinding(Queue nodesAddressesQueue, FanoutExchange nodesAddressesExchange) {
        return BindingBuilder.bind(nodesAddressesQueue).to(nodesAddressesExchange);
    }

    @Bean
    public FanoutExchange blockExchange() {
        return new FanoutExchange(BLOCK_EXCHANGE);
    }

    @Bean
    public Queue blockQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding blockBinding(Queue blockQueue, FanoutExchange blockExchange) {
        return BindingBuilder.bind(blockQueue).to(blockExchange);
    }

    @Bean
    public FanoutExchange transactionExchange() {
        return new FanoutExchange(TRANSACTION_EXCHANGE);
    }

    @Bean
    public Queue transactionQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding transactionBinding(Queue transactionQueue, FanoutExchange transactionExchange) {
        return BindingBuilder.bind(transactionQueue).to(transactionExchange);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
