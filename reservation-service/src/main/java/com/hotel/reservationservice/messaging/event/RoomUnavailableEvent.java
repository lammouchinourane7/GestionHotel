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
public class RoomUnavailableEvent {

    private Long roomId;
    private String roomNumber;
    private LocalDateTime occurredAt;
}
