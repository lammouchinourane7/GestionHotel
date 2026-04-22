package com.hotel.clientservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreatedEvent {

    private Long clientId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
}
