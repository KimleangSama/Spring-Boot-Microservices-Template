package com.keakimleang.notificationservice;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
    @Value("${queue.order-placed.name}")
    private String queueName;
    @Value("${queue.order-placed.exchange-name}")
    private String exchangeName;
    @Value("${queue.order-placed.routing-key}")
    private String routingKey;
    @Value("${queue.order-placed.durable}")
    private boolean durable;
    @Value("${queue.order-placed.auto-delete}")
    private boolean autoDelete;

    @Bean
    public Queue orderPlacedQueue() {
        return new Queue(queueName, durable);
    }

    @Bean
    public TopicExchange orderPlacedExchange() {
        return new TopicExchange(exchangeName, durable, autoDelete);
    }

    @Bean
    public Binding binding(Queue urlClickCountQueue, TopicExchange urlClickCountExchange) {
        return BindingBuilder
                .bind(urlClickCountQueue)
                .to(urlClickCountExchange)
                .with(routingKey);
    }

    @Bean
    public Declarables declarable(Queue urlClickCountQueue, TopicExchange urlClickCountExchange, Binding binding) {
        return new Declarables(urlClickCountQueue, urlClickCountExchange, binding);
    }

    @Bean
    public Connection connection(RabbitProps rabbitProps) {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.useNio();
            connectionFactory.setHost(rabbitProps.getHost());
            connectionFactory.setPort(rabbitProps.getPort());
            connectionFactory.setUsername(rabbitProps.getUsername());
            connectionFactory.setPassword(rabbitProps.getPassword());
            Connection connection = connectionFactory.newConnection();
            log.info("RabbitMQ connection established successfully to {}:{}",
                    rabbitProps.getHost(), rabbitProps.getPort());
            return connection;
        } catch (Exception e) {
            log.error("Failed to establish RabbitMQ connection: {}", e.getMessage());
            return null;
        }
    }
}
