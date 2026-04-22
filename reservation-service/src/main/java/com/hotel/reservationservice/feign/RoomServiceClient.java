package com.hotel.reservationservice.feign;

import com.hotel.reservationservice.config.FeignConfig;
import com.hotel.reservationservice.dto.RoomAvailabilityResponse;
import com.hotel.reservationservice.dto.RoomExistenceResponse;
import com.hotel.reservationservice.dto.RoomPriceResponse;
import com.hotel.reservationservice.dto.RoomSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "room-service", configuration = FeignConfig.class)
public interface RoomServiceClient {

    @GetMapping("/api/rooms/{id}/exists")
    RoomExistenceResponse existsById(@PathVariable("id") Long id);

    @GetMapping("/api/rooms/{id}/availability")
    RoomAvailabilityResponse isRoomAvailable(@PathVariable("id") Long id);

    @GetMapping("/api/rooms/{id}/price")
    RoomPriceResponse getRoomPrice(@PathVariable("id") Long id);

    @GetMapping("/api/rooms/{id}")
    RoomSummaryResponse getRoomById(@PathVariable("id") Long id);

    @GetMapping("/api/rooms")
    List<RoomSummaryResponse> getAllRooms();
}
