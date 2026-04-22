package com.hotel.reservationservice.service;

import com.hotel.reservationservice.dto.ReservationDetailsResponse;
import com.hotel.reservationservice.dto.ReservationRequest;
import com.hotel.reservationservice.dto.ReservationResponse;
import com.hotel.reservationservice.dto.ClientSummaryResponse;
import com.hotel.reservationservice.dto.RoomSummaryResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request);

    ReservationResponse createReservationForUser(String email, ReservationRequest request);

    List<ReservationResponse> getAllReservations();

    List<ReservationResponse> getReservationsForUser(String email);

    ReservationResponse getReservationById(Long id);

    ReservationResponse getReservationByIdForUser(Long id, String email);

    ReservationDetailsResponse getReservationDetails(Long id);

    ReservationDetailsResponse getReservationDetailsForUser(Long id, String email);

    void deleteReservation(Long id);

    ReservationResponse confirmReservation(Long id);

    ReservationResponse cancelReservation(Long id);

    List<ReservationResponse> getReservationsByClientId(Long clientId);

    List<ReservationResponse> getReservationsByRoomId(Long roomId);

    List<ClientSummaryResponse> getClientOptions();

    List<RoomSummaryResponse> getRoomOptions();

    void cancelReservationsByClient(Long clientId);

    void cancelReservationsByRoom(Long roomId);
}
