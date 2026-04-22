package com.hotel.roomservice.mapper;

import com.hotel.roomservice.dto.RoomRequest;
import com.hotel.roomservice.dto.RoomResponse;
import com.hotel.roomservice.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequest request) {
        return Room.builder()
                .number(request.getNumber())
                .type(request.getType())
                .capacity(request.getCapacity())
                .pricePerNight(request.getPricePerNight())
                .available(request.getAvailable() == null || request.getAvailable())
                .build();
    }

    public void updateEntity(Room room, RoomRequest request) {
        room.setNumber(request.getNumber());
        room.setType(request.getType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        room.setAvailable(request.getAvailable() == null || request.getAvailable());
    }

    public RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .number(room.getNumber())
                .type(room.getType())
                .capacity(room.getCapacity())
                .pricePerNight(room.getPricePerNight())
                .available(room.isAvailable())
                .build();
    }
}
