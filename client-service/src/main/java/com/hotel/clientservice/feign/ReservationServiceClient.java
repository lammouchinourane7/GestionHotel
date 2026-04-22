package com.hotel.clientservice.feign;

import com.hotel.clientservice.dto.ClientReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "reservation-service")
public interface ReservationServiceClient {

    @GetMapping("/api/reservations")
    List<ClientReservationResponse> getAllReservations();

    @GetMapping("/api/reservations/client/{clientId}")
    List<ClientReservationResponse> getReservationsByClientId(@PathVariable("clientId") Long clientId);
}
