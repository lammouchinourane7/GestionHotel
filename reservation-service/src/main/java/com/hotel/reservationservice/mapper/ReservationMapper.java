package com.hotel.reservationservice.mapper;

import com.hotel.reservationservice.dto.ClientSummaryResponse;
import com.hotel.reservationservice.dto.ReservationDetailsResponse;
import com.hotel.reservationservice.dto.ReservationRequest;
import com.hotel.reservationservice.dto.ReservationResponse;
import com.hotel.reservationservice.dto.RoomSummaryResponse;
import com.hotel.reservationservice.entity.Reservation;
import com.hotel.reservationservice.entity.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequest request, double totalPrice, ReservationStatus status) {
        return Reservation.builder()
                .clientId(request.getClientId())
                .roomId(request.getRoomId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalPrice(totalPrice)
                .status(status)
                .build();
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .clientId(reservation.getClientId())
                .roomId(reservation.getRoomId())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .build();
    }

    public ReservationDetailsResponse toDetailsResponse(Reservation reservation,
                                                        ClientSummaryResponse client,
                                                        RoomSummaryResponse room) {
        return ReservationDetailsResponse.builder()
                .id(reservation.getId())
                .clientId(reservation.getClientId())
                .roomId(reservation.getRoomId())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .client(client)
                .room(room)
                .build();
    }
}
