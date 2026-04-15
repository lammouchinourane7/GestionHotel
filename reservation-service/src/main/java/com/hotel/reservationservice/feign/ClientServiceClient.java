package com.hotel.reservationservice.feign;

import com.hotel.reservationservice.config.FeignConfig;
import com.hotel.reservationservice.dto.ClientExistenceResponse;
import com.hotel.reservationservice.dto.ClientSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service", configuration = FeignConfig.class)
public interface ClientServiceClient {

    @GetMapping("/api/clients/{id}/exists")
    ClientExistenceResponse existsById(@PathVariable("id") Long id);

    @GetMapping("/api/clients/{id}")
    ClientSummaryResponse getClientById(@PathVariable("id") Long id);
}
