package com.hotel.clientservice.messaging.producer;

import com.hotel.clientservice.config.RabbitMQConfig;
import com.hotel.clientservice.messaging.event.ClientCreatedEvent;
import com.hotel.clientservice.messaging.event.ClientDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishClientCreated(ClientCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.HOTEL_EVENTS_EXCHANGE,
                RabbitMQConfig.CLIENT_CREATED_ROUTING_KEY,
                event
        );
    }

    public void publishClientDeleted(ClientDeletedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.HOTEL_EVENTS_EXCHANGE,
                RabbitMQConfig.CLIENT_DELETED_ROUTING_KEY,
                event
        );
    }
}
