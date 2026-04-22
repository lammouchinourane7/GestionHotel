package com.hotel.clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientHistoryResponse {

    private Long clientId;
    private String clientFullName;
    private long reservationCount;
    private long confirmedReservations;
    private long createdReservations;
    private long cancelledReservations;
    private double totalSpent;
    private double averageSpent;
    private LocalDate firstReservationDate;
    private LocalDate lastReservationDate;
    private LocalDate nextReservationDate;
    private List<ClientReservationResponse> reservations;
}
