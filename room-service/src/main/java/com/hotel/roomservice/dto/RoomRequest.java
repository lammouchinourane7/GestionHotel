package com.hotel.roomservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    @NotBlank(message = "Room number is required")
    private String number;

    @NotBlank(message = "Room type is required")
    private String type;

    @Positive(message = "Capacity must be greater than 0")
    private int capacity;

    @Positive(message = "Price per night must be greater than 0")
    private double pricePerNight;

    private Boolean available;
}
