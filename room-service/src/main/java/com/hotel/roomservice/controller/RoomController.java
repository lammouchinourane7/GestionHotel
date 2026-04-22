package com.hotel.roomservice.controller;

import com.hotel.roomservice.dto.RoomAvailabilityResponse;
import com.hotel.roomservice.dto.RoomExistenceResponse;
import com.hotel.roomservice.dto.RoomPriceResponse;
import com.hotel.roomservice.dto.RoomRequest;
import com.hotel.roomservice.dto.RoomResponse;
import com.hotel.roomservice.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Service", description = "Gestion complete des chambres")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @Operation(summary = "Recuperer toutes les chambres")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer une chambre par son id")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    @Operation(summary = "Ajouter une chambre")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour une chambre")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une chambre")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/type/{type}")
    @Operation(summary = "Rechercher des chambres par type")
    public ResponseEntity<List<RoomResponse>> searchByType(@PathVariable String type) {
        return ResponseEntity.ok(roomService.searchByType(type));
    }

    @GetMapping("/search/availability/{available}")
    @Operation(summary = "Rechercher des chambres par disponibilite")
    public ResponseEntity<List<RoomResponse>> searchByAvailability(@PathVariable boolean available) {
        return ResponseEntity.ok(roomService.searchByAvailability(available));
    }

    @GetMapping("/search/capacity/{capacity}")
    @Operation(summary = "Rechercher des chambres par capacite")
    public ResponseEntity<List<RoomResponse>> searchByCapacity(@PathVariable int capacity) {
        return ResponseEntity.ok(roomService.searchByCapacity(capacity));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Verifier si une chambre existe")
    public ResponseEntity<RoomExistenceResponse> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.existsById(id));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Verifier si une chambre est disponible")
    public ResponseEntity<RoomAvailabilityResponse> isRoomAvailable(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.isRoomAvailable(id));
    }

    @GetMapping("/{id}/price")
    @Operation(summary = "Recuperer le prix par nuit d'une chambre")
    public ResponseEntity<RoomPriceResponse> getRoomPrice(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomPrice(id));
    }
}
