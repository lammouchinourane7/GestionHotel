package com.hotel.reservationservice.messaging.producer;

import com.hotel.reservationservice.config.RabbitMQConfig;
import com.hotel.reservationservice.messaging.event.ReservationCancelledEvent;
import com.hotel.reservationservice.messaging.event.ReservationConfirmedEvent;
import com.hotel.reservationservice.messaging.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishReservationCreated(ReservationCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.HOTEL_EVENTS_EXCHANGE,
                RabbitMQConfig.RESERVATION_CREATED_ROUTING_KEY,
                event
        );
    }

    public void publishReservationCancelled(ReservationCancelledEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.HOTEL_EVENTS_EXCHANGE,
                RabbitMQConfig.RESERVATION_CANCELLED_ROUTING_KEY,
                event
        );
    }

    public void publishReservationConfirmed(ReservationConfirmedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.HOTEL_EVENTS_EXCHANGE,
                RabbitMQConfig.RESERVATION_CONFIRMED_ROUTING_KEY,
                event
        );
    }
}
