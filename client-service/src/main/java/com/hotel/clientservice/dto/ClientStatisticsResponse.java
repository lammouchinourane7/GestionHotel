package com.hotel.clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatisticsResponse {

    private long totalClients;
    private long newClientsLast30Days;
    private long clientsWithPreferences;
    private long clientsWithReservations;
    private long totalReservations;
    private long confirmedReservations;
    private long cancelledReservations;
    private double totalRevenue;
    private double averageReservationsPerClient;
    private double averageRevenuePerClient;
    private Map<String, Long> loyaltyDistribution;
}
