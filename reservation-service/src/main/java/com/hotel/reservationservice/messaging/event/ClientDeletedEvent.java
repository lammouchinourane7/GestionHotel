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
public class ClientDeletedEvent {

    private Long clientId;
    private String email;
    private LocalDateTime deletedAt;
}
