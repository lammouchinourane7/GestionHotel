package com.hotel.clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfileResponse {

    private ClientResponse client;
    private ClientStayPreferencesResponse stayPreferences;
    private ClientLoyaltyResponse loyalty;
    private ClientHistoryResponse historySummary;
    private List<ClientReservationResponse> recentReservations;
}
