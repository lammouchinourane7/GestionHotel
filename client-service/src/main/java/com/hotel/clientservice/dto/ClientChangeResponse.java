package com.hotel.clientservice.dto;

import com.hotel.clientservice.entity.ClientChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientChangeResponse {

    private Long id;
    private Long clientId;
    private ClientChangeType action;
    private String details;
    private String changedBy;
    private LocalDateTime changedAt;
}
