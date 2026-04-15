package com.hotel.reservationservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreatedEvent {

    private Long reservationId;
    private Long clientId;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private String status;
    private LocalDateTime occurredAt;
}
