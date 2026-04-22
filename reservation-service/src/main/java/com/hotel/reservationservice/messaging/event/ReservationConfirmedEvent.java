package com.hotel.reservationservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationConfirmedEvent {

    private Long reservationId;
    private Long clientId;
    private Long roomId;
    private String status;
    private LocalDateTime occurredAt;
}
