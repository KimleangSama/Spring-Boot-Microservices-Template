package com.keakimleang.notificationservice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class RabbitProps {
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
    @Value("${spring.rabbitmq.host}")
    public String host;
    @Value("${spring.rabbitmq.port}")
    public int port;
    @Value("${spring.rabbitmq.username}")
    public String username;
    @Value("${spring.rabbitmq.password}")
    public String password;
}
