package com.hotel.reservationservice.service.impl;

import com.hotel.reservationservice.dto.ClientExistenceResponse;
import com.hotel.reservationservice.dto.ClientSummaryResponse;
import com.hotel.reservationservice.dto.ReservationDetailsResponse;
import com.hotel.reservationservice.dto.ReservationRequest;
import com.hotel.reservationservice.dto.ReservationResponse;
import com.hotel.reservationservice.dto.RoomAvailabilityResponse;
import com.hotel.reservationservice.dto.RoomExistenceResponse;
import com.hotel.reservationservice.dto.RoomPriceResponse;
import com.hotel.reservationservice.dto.RoomSummaryResponse;
import com.hotel.reservationservice.entity.Reservation;
import com.hotel.reservationservice.entity.ReservationStatus;
import com.hotel.reservationservice.exception.ClientNotFoundException;
import com.hotel.reservationservice.exception.InvalidReservationDateException;
import com.hotel.reservationservice.exception.RemoteServiceException;
import com.hotel.reservationservice.exception.ReservationNotFoundException;
import com.hotel.reservationservice.exception.RoomNotAvailableException;
import com.hotel.reservationservice.exception.RoomNotFoundException;
import com.hotel.reservationservice.feign.ClientServiceClient;
import com.hotel.reservationservice.feign.RoomServiceClient;
import com.hotel.reservationservice.mapper.ReservationMapper;
import com.hotel.reservationservice.messaging.event.ReservationCancelledEvent;
import com.hotel.reservationservice.messaging.event.ReservationConfirmedEvent;
import com.hotel.reservationservice.messaging.event.ReservationCreatedEvent;
import com.hotel.reservationservice.messaging.producer.ReservationEventProducer;
import com.hotel.reservationservice.repository.ReservationRepository;
import com.hotel.reservationservice.service.ReservationService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationEventProducer reservationEventProducer;
    private final ClientServiceClient clientServiceClient;
    private final RoomServiceClient roomServiceClient;

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {
        validateReservationDates(request);
        ensureClientExists(request.getClientId());
        ensureRoomExists(request.getRoomId());
        ensureRoomAvailable(request.getRoomId());

        RoomPriceResponse priceResponse = fetchRoomPrice(request.getRoomId());
        long numberOfNights = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        double totalPrice = priceResponse.getPricePerNight() * numberOfNights;

        Reservation reservation = reservationMapper.toEntity(request, totalPrice, ReservationStatus.CREATED);
        Reservation savedReservation = reservationRepository.save(reservation);

        reservationEventProducer.publishReservationCreated(ReservationCreatedEvent.builder()
                .reservationId(savedReservation.getId())
                .clientId(savedReservation.getClientId())
                .roomId(savedReservation.getRoomId())
                .startDate(savedReservation.getStartDate())
                .endDate(savedReservation.getEndDate())
                .totalPrice(savedReservation.getTotalPrice())
                .status(savedReservation.getStatus().name())
                .occurredAt(LocalDateTime.now())
                .build());

        return reservationMapper.toResponse(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        return reservationMapper.toResponse(findReservationById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDetailsResponse getReservationDetails(Long id) {
        Reservation reservation = findReservationById(id);
        ClientSummaryResponse client = fetchClientById(reservation.getClientId());
        RoomSummaryResponse room = fetchRoomById(reservation.getRoomId());
        return reservationMapper.toDetailsResponse(reservation, client, room);
    }

    @Override
    public void deleteReservation(Long id) {
        Reservation reservation = findReservationById(id);
        reservationRepository.delete(reservation);
    }

    @Override
    public ReservationResponse confirmReservation(Long id) {
        Reservation reservation = findReservationById(id);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation updatedReservation = reservationRepository.save(reservation);

        reservationEventProducer.publishReservationConfirmed(ReservationConfirmedEvent.builder()
                .reservationId(updatedReservation.getId())
                .clientId(updatedReservation.getClientId())
                .roomId(updatedReservation.getRoomId())
                .status(updatedReservation.getStatus().name())
                .occurredAt(LocalDateTime.now())
                .build());

        return reservationMapper.toResponse(updatedReservation);
    }

    @Override
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = findReservationById(id);
        cancelReservationEntity(reservation, "Cancelled manually");
        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByClientId(Long clientId) {
        return reservationRepository.findByClientId(clientId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByRoomId(Long roomId) {
        return reservationRepository.findByRoomId(roomId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public void cancelReservationsByClient(Long clientId) {
        reservationRepository.findByClientId(clientId)
                .stream()
                .filter(reservation -> reservation.getStatus() != ReservationStatus.CANCELLED)
                .forEach(reservation -> cancelReservationEntity(reservation, "Cancelled after client deletion"));
    }

    @Override
    public void cancelReservationsByRoom(Long roomId) {
        reservationRepository.findByRoomId(roomId)
                .stream()
                .filter(reservation -> reservation.getStatus() != ReservationStatus.CANCELLED)
                .forEach(reservation -> cancelReservationEntity(reservation, "Cancelled after room became unavailable"));
    }

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + id));
    }

    private void validateReservationDates(ReservationRequest request) {
        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new InvalidReservationDateException("Start date must be before end date");
        }
    }

    private void ensureClientExists(Long clientId) {
        try {
            ClientExistenceResponse response = clientServiceClient.existsById(clientId);
            if (!response.isExists()) {
                throw new ClientNotFoundException("Client not found with id: " + clientId);
            }
        } catch (FeignException exception) {
            throw mapClientFeignException(clientId, exception);
        }
    }

    private void ensureRoomExists(Long roomId) {
        try {
            RoomExistenceResponse response = roomServiceClient.existsById(roomId);
            if (!response.isExists()) {
                throw new RoomNotFoundException("Room not found with id: " + roomId);
            }
        } catch (FeignException exception) {
            throw mapRoomFeignException(roomId, exception);
        }
    }

    private void ensureRoomAvailable(Long roomId) {
        try {
            RoomAvailabilityResponse response = roomServiceClient.isRoomAvailable(roomId);
            if (!response.isAvailable()) {
                throw new RoomNotAvailableException("Room is not available for reservation: " + roomId);
            }
        } catch (FeignException.NotFound exception) {
            throw new RoomNotFoundException("Room not found with id: " + roomId);
        } catch (FeignException exception) {
            throw new RemoteServiceException("Room service is unavailable");
        }
    }

    private RoomPriceResponse fetchRoomPrice(Long roomId) {
        try {
            return roomServiceClient.getRoomPrice(roomId);
        } catch (FeignException.NotFound exception) {
            throw new RoomNotFoundException("Room not found with id: " + roomId);
        } catch (FeignException exception) {
            throw new RemoteServiceException("Room service is unavailable");
        }
    }

    private ClientSummaryResponse fetchClientById(Long clientId) {
        try {
            return clientServiceClient.getClientById(clientId);
        } catch (FeignException.NotFound exception) {
            throw new ClientNotFoundException("Client not found with id: " + clientId);
        } catch (FeignException exception) {
            throw new RemoteServiceException("Client service is unavailable");
        }
    }

    private RoomSummaryResponse fetchRoomById(Long roomId) {
        try {
            return roomServiceClient.getRoomById(roomId);
        } catch (FeignException.NotFound exception) {
            throw new RoomNotFoundException("Room not found with id: " + roomId);
        } catch (FeignException exception) {
            throw new RemoteServiceException("Room service is unavailable");
        }
    }

    private RuntimeException mapClientFeignException(Long clientId, FeignException exception) {
        if (exception instanceof FeignException.NotFound) {
            return new ClientNotFoundException("Client not found with id: " + clientId);
        }
        return new RemoteServiceException("Client service is unavailable");
    }

    private RuntimeException mapRoomFeignException(Long roomId, FeignException exception) {
        if (exception instanceof FeignException.NotFound) {
            return new RoomNotFoundException("Room not found with id: " + roomId);
        }
        return new RemoteServiceException("Room service is unavailable");
    }

    private void cancelReservationEntity(Reservation reservation, String reason) {
        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation updatedReservation = reservationRepository.save(reservation);

        reservationEventProducer.publishReservationCancelled(ReservationCancelledEvent.builder()
                .reservationId(updatedReservation.getId())
                .clientId(updatedReservation.getClientId())
                .roomId(updatedReservation.getRoomId())
                .status(updatedReservation.getStatus().name())
                .reason(reason)
                .occurredAt(LocalDateTime.now())
                .build());
    }
}
