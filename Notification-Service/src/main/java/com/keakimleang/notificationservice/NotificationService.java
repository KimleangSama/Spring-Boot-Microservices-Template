package com.keakimleang.notificationservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    @RabbitListener(queues = "${queue.order-placed.name}")
    public void handleOrderPlaced(String orderNumber) {
        // Simulate sending notification (e.g., email, SMS)
        log.info("Here is your order number: {} for tracking.", orderNumber);
    }
}
