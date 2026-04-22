package com.hotel.reservationservice.controller;

import com.hotel.reservationservice.dto.ClientSummaryResponse;
import com.hotel.reservationservice.dto.ReservationDetailsResponse;
import com.hotel.reservationservice.dto.ReservationRequest;
import com.hotel.reservationservice.dto.ReservationResponse;
import com.hotel.reservationservice.dto.RoomSummaryResponse;
import com.hotel.reservationservice.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Service", description = "Gestion complete des reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Recuperer toutes les reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservations(Authentication authentication,
                                                                        @AuthenticationPrincipal Jwt jwt) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(reservationService.getAllReservations());
        }

        return ResponseEntity.ok(reservationService.getReservationsForUser(getUserEmail(jwt)));
    }

    @GetMapping("/me")
    @Operation(summary = "Recuperer les reservations de l'utilisateur connecte")
    public ResponseEntity<List<ReservationResponse>> getCurrentUserReservations(Authentication authentication,
                                                                                @AuthenticationPrincipal Jwt jwt) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(reservationService.getAllReservations());
        }

        return ResponseEntity.ok(reservationService.getReservationsForUser(getUserEmail(jwt)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer une reservation par son id")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id,
                                                                  Authentication authentication,
                                                                  @AuthenticationPrincipal Jwt jwt) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(reservationService.getReservationById(id));
        }

        return ResponseEntity.ok(reservationService.getReservationByIdForUser(id, getUserEmail(jwt)));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Recuperer une reservation enrichie via OpenFeign")
    public ResponseEntity<ReservationDetailsResponse> getReservationDetails(@PathVariable Long id,
                                                                            Authentication authentication,
                                                                            @AuthenticationPrincipal Jwt jwt) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(reservationService.getReservationDetails(id));
        }

        return ResponseEntity.ok(reservationService.getReservationDetailsForUser(id, getUserEmail(jwt)));
    }

    @PostMapping
    @Operation(summary = "Creer une reservation avec verifications OpenFeign")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request,
                                                                 Authentication authentication,
                                                                 @AuthenticationPrincipal Jwt jwt) {
        ReservationResponse response = isAdmin(authentication)
                ? reservationService.createReservation(request)
                : reservationService.createReservationForUser(getUserEmail(jwt), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une reservation")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une reservation")
    public ResponseEntity<ReservationResponse> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annuler une reservation")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Recuperer les reservations d'un client")
    public ResponseEntity<List<ReservationResponse>> getReservationsByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(reservationService.getReservationsByClientId(clientId));
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Recuperer les reservations d'une chambre")
    public ResponseEntity<List<ReservationResponse>> getReservationsByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(reservationService.getReservationsByRoomId(roomId));
    }

    @GetMapping("/options/clients")
    @Operation(summary = "Recuperer les clients via reservation-service OpenFeign")
    public ResponseEntity<List<ClientSummaryResponse>> getReservationClientOptions() {
        return ResponseEntity.ok(reservationService.getClientOptions());
    }

    @GetMapping("/options/rooms")
    @Operation(summary = "Recuperer les chambres via reservation-service OpenFeign")
    public ResponseEntity<List<RoomSummaryResponse>> getReservationRoomOptions() {
        return ResponseEntity.ok(reservationService.getRoomOptions());
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_HOTEL_ADMIN"::equals);
    }

    private String getUserEmail(Jwt jwt) {
        if (jwt == null) {
            return null;
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return email;
        }

        return jwt.getClaimAsString("preferred_username");
    }
}
