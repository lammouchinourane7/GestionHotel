package com.hotel.roomservice.service.impl;

import com.hotel.roomservice.dto.RoomAvailabilityResponse;
import com.hotel.roomservice.dto.RoomExistenceResponse;
import com.hotel.roomservice.dto.RoomPriceResponse;
import com.hotel.roomservice.dto.RoomRequest;
import com.hotel.roomservice.dto.RoomResponse;
import com.hotel.roomservice.entity.Room;
import com.hotel.roomservice.exception.RoomNotFoundException;
import com.hotel.roomservice.mapper.RoomMapper;
import com.hotel.roomservice.messaging.event.RoomUnavailableEvent;
import com.hotel.roomservice.messaging.event.RoomUpdatedEvent;
import com.hotel.roomservice.messaging.producer.RoomEventProducer;
import com.hotel.roomservice.repository.RoomRepository;
import com.hotel.roomservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final RoomEventProducer roomEventProducer;

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        Room savedRoom = roomRepository.save(roomMapper.toEntity(request));
        return roomMapper.toResponse(savedRoom);
    }

    @Override
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room existingRoom = findRoomById(id);
        boolean wasAvailable = existingRoom.isAvailable();
        roomMapper.updateEntity(existingRoom, request);
        Room updatedRoom = roomRepository.save(existingRoom);

        roomEventProducer.publishRoomUpdated(RoomUpdatedEvent.builder()
                .roomId(updatedRoom.getId())
                .number(updatedRoom.getNumber())
                .type(updatedRoom.getType())
                .capacity(updatedRoom.getCapacity())
                .pricePerNight(updatedRoom.getPricePerNight())
                .available(updatedRoom.isAvailable())
                .updatedAt(LocalDateTime.now())
                .build());

        if (wasAvailable && !updatedRoom.isAvailable()) {
            roomEventProducer.publishRoomUnavailable(RoomUnavailableEvent.builder()
                    .roomId(updatedRoom.getId())
                    .roomNumber(updatedRoom.getNumber())
                    .occurredAt(LocalDateTime.now())
                    .build());
        }

        return roomMapper.toResponse(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        Room existingRoom = findRoomById(id);
        roomRepository.delete(existingRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        return roomMapper.toResponse(findRoomById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> searchByType(String type) {
        return roomRepository.findByTypeContainingIgnoreCase(type)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> searchByCapacity(int capacity) {
        return roomRepository.findByCapacity(capacity)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> searchByAvailability(boolean available) {
        return roomRepository.findByAvailable(available)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomExistenceResponse existsById(Long id) {
        return RoomExistenceResponse.builder()
                .roomId(id)
                .exists(roomRepository.existsById(id))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomAvailabilityResponse isRoomAvailable(Long id) {
        Room room = findRoomById(id);
        return RoomAvailabilityResponse.builder()
                .roomId(id)
                .available(room.isAvailable())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomPriceResponse getRoomPrice(Long id) {
        Room room = findRoomById(id);
        return RoomPriceResponse.builder()
                .roomId(id)
                .pricePerNight(room.getPricePerNight())
                .build();
    }

    private Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + id));
    }
}
