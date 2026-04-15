package com.hotel.clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoyaltyResponse {

    private Long clientId;
    private ClientLoyaltyStatus status;
    private int points;
    private long reservationCount;
    private long confirmedReservations;
    private long createdReservations;
    private long cancelledReservations;
    private double totalSpent;
    private double averageSpent;
}
