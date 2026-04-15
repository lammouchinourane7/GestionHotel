package com.hotel.clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientReservationResponse {

    private Long id;
    private Long clientId;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private String status;
}
