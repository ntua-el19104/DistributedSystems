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

    public static final String CONNECTION_QUEUE = "connection_queue";
    public static final String ACCEPTED_CONNECTIONS_QUEUE = "accepted_connections_queue";
    public static final String EXCHANGE = "connection_exchange";
    public static final String CONNECTION_ROUTING_KEY = "connection_routingKey";
    public static final String ACCEPTED_CONNECTIONS_ROUTING_KEY = "accepted_connections_routingKey";

    @Bean
    public Queue queue() {
        return new Queue(CONNECTION_QUEUE);
    }

    @Bean
    public Queue repliesQueue() {
        return new Queue(ACCEPTED_CONNECTIONS_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding firstBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(CONNECTION_ROUTING_KEY);
    }

    @Bean
    public Binding secondBinding(Queue repliesQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(repliesQueue)
                .to(exchange)
                .with(ACCEPTED_CONNECTIONS_ROUTING_KEY);
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

