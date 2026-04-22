package com.hotel.reservationservice.messaging.consumer;

import com.hotel.reservationservice.config.RabbitMQConfig;
import com.hotel.reservationservice.messaging.event.ClientDeletedEvent;
import com.hotel.reservationservice.messaging.event.RoomUnavailableEvent;
import com.hotel.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventConsumer {

    private final ReservationService reservationService;

    @RabbitListener(queues = RabbitMQConfig.CLIENT_DELETED_QUEUE)
    public void handleClientDeletedEvent(ClientDeletedEvent event) {
        reservationService.cancelReservationsByClient(event.getClientId());
    }

    @RabbitListener(queues = RabbitMQConfig.ROOM_UNAVAILABLE_QUEUE)
    public void handleRoomUnavailableEvent(RoomUnavailableEvent event) {
        reservationService.cancelReservationsByRoom(event.getRoomId());
    }
}
