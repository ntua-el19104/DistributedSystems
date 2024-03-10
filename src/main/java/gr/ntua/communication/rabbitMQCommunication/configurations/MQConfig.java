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
