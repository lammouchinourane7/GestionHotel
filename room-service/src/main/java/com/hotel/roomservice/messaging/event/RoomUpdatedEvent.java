package com.hotel.roomservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomUpdatedEvent {

    private Long roomId;
    private String number;
    private String type;
    private int capacity;
    private double pricePerNight;
    private boolean available;
    private LocalDateTime updatedAt;
}
