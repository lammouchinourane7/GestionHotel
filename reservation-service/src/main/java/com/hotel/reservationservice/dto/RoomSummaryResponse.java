package com.hotel.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSummaryResponse {

    private Long id;
    private String number;
    private String type;
    private int capacity;
    private double pricePerNight;
    private boolean available;
}
