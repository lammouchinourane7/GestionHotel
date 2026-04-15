package com.hotel.clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String nationalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ClientStayPreferencesResponse stayPreferences;
}
