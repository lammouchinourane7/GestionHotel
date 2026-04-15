package com.hotel.roomservice.service;

import com.hotel.roomservice.dto.RoomAvailabilityResponse;
import com.hotel.roomservice.dto.RoomExistenceResponse;
import com.hotel.roomservice.dto.RoomPriceResponse;
import com.hotel.roomservice.dto.RoomRequest;
import com.hotel.roomservice.dto.RoomResponse;

import java.util.List;

public interface RoomService {

    RoomResponse createRoom(RoomRequest request);

    RoomResponse updateRoom(Long id, RoomRequest request);

    void deleteRoom(Long id);

    List<RoomResponse> getAllRooms();

    RoomResponse getRoomById(Long id);

    List<RoomResponse> searchByType(String type);

    List<RoomResponse> searchByCapacity(int capacity);

    List<RoomResponse> searchByAvailability(boolean available);

    RoomExistenceResponse existsById(Long id);

    RoomAvailabilityResponse isRoomAvailable(Long id);

    RoomPriceResponse getRoomPrice(Long id);
}
