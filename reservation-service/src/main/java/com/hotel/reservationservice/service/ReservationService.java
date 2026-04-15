package com.hotel.reservationservice.service;

import com.hotel.reservationservice.dto.ReservationDetailsResponse;
import com.hotel.reservationservice.dto.ReservationRequest;
import com.hotel.reservationservice.dto.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request);

    List<ReservationResponse> getAllReservations();

    ReservationResponse getReservationById(Long id);

    ReservationDetailsResponse getReservationDetails(Long id);

    void deleteReservation(Long id);

    ReservationResponse confirmReservation(Long id);

    ReservationResponse cancelReservation(Long id);

    List<ReservationResponse> getReservationsByClientId(Long clientId);

    List<ReservationResponse> getReservationsByRoomId(Long roomId);

    void cancelReservationsByClient(Long clientId);

    void cancelReservationsByRoom(Long roomId);
}
