package com.hotel.roomservice.messaging.producer;

import com.hotel.roomservice.config.RabbitMQConfig;
import com.hotel.roomservice.messaging.event.RoomUnavailableEvent;
import com.hotel.roomservice.messaging.event.RoomUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishRoomUpdated(RoomUpdatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.HOTEL_EVENTS_EXCHANGE,
                RabbitMQConfig.ROOM_UPDATED_ROUTING_KEY,
                event
        );
    }

    public void publishRoomUnavailable(RoomUnavailableEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.HOTEL_EVENTS_EXCHANGE,
                RabbitMQConfig.ROOM_UNAVAILABLE_ROUTING_KEY,
                event
        );
    }
}
