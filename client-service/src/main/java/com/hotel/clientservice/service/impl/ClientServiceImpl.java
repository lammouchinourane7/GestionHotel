package com.hotel.clientservice.service.impl;

import com.hotel.clientservice.dto.ClientChangeResponse;
import com.hotel.clientservice.dto.ClientExistenceResponse;
import com.hotel.clientservice.dto.ClientHistoryResponse;
import com.hotel.clientservice.dto.ClientLoyaltyResponse;
import com.hotel.clientservice.dto.ClientLoyaltyStatus;
import com.hotel.clientservice.dto.ClientPageResponse;
import com.hotel.clientservice.dto.ClientPageSearchMode;
import com.hotel.clientservice.dto.ClientProfileResponse;
import com.hotel.clientservice.dto.ClientRequest;
import com.hotel.clientservice.dto.ClientReservationResponse;
import com.hotel.clientservice.dto.ClientResponse;
import com.hotel.clientservice.dto.ClientStatisticsResponse;
import com.hotel.clientservice.dto.ClientStayPreferencesRequest;
import com.hotel.clientservice.dto.ClientStayPreferencesResponse;
import com.hotel.clientservice.entity.Client;
import com.hotel.clientservice.entity.ClientChangeHistory;
import com.hotel.clientservice.entity.ClientChangeType;
import com.hotel.clientservice.entity.ClientStayPreferences;
import com.hotel.clientservice.exception.ClientNotFoundException;
import com.hotel.clientservice.exception.EmailAlreadyExistsException;
import com.hotel.clientservice.exception.RemoteServiceException;
import com.hotel.clientservice.feign.ReservationServiceClient;
import com.hotel.clientservice.mapper.ClientMapper;
import com.hotel.clientservice.messaging.event.ClientCreatedEvent;
import com.hotel.clientservice.messaging.event.ClientDeletedEvent;
import com.hotel.clientservice.messaging.producer.ClientEventProducer;
import com.hotel.clientservice.repository.ClientChangeHistoryRepository;
import com.hotel.clientservice.repository.ClientRepository;
import com.hotel.clientservice.service.ClientService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private static final String SYSTEM_USER = "system";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "firstName", "lastName", "email", "createdAt", "updatedAt"
    );

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientEventProducer clientEventProducer;
    private final ClientChangeHistoryRepository clientChangeHistoryRepository;
    private final ReservationServiceClient reservationServiceClient;

    @Override
    public ClientResponse createClient(ClientRequest request) {
        validateEmailUniqueness(request.getEmail(), null);
        Client savedClient = clientRepository.save(clientMapper.toEntity(request));

        recordChange(savedClient.getId(), ClientChangeType.CREATED, "Client profile created.");

        clientEventProducer.publishClientCreated(ClientCreatedEvent.builder()
                .clientId(savedClient.getId())
                .firstName(savedClient.getFirstName())
                .lastName(savedClient.getLastName())
                .email(savedClient.getEmail())
                .createdAt(savedClient.getCreatedAt())
                .build());

        return clientMapper.toResponse(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        return clientMapper.toResponse(findClientById(id));
    }

    @Override
    public ClientResponse updateClient(Long id, ClientRequest request) {
        Client existingClient = findClientById(id);
        validateEmailUniqueness(request.getEmail(), id);

        List<String> profileChanges = collectProfileChanges(existingClient, request);
        List<String> preferenceChanges = request.getStayPreferences() == null
                ? List.of()
                : collectPreferenceChanges(existingClient.getStayPreferences(), request.getStayPreferences());

        clientMapper.updateEntity(existingClient, request);
        Client updatedClient = clientRepository.save(existingClient);

        if (!profileChanges.isEmpty()) {
            recordChange(updatedClient.getId(), ClientChangeType.UPDATED,
                    "Updated profile fields: " + String.join(", ", profileChanges) + ".");
        }
        if (!preferenceChanges.isEmpty()) {
            recordChange(updatedClient.getId(), ClientChangeType.PREFERENCES_UPDATED,
                    "Updated stay preferences: " + String.join(", ", preferenceChanges) + ".");
        }

        return clientMapper.toResponse(updatedClient);
    }

    @Override
    public void deleteClient(Long id) {
        Client existingClient = findClientById(id);
        clientRepository.delete(existingClient);

        recordChange(existingClient.getId(), ClientChangeType.DELETED, "Client deleted from the service.");

        clientEventProducer.publishClientDeleted(ClientDeletedEvent.builder()
                .clientId(existingClient.getId())
                .email(existingClient.getEmail())
                .deletedAt(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> searchByFirstName(String firstName) {
        return clientRepository.findByFirstNameContainingIgnoreCase(firstName)
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> searchByLastName(String lastName) {
        return clientRepository.findByLastNameContainingIgnoreCase(lastName)
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse searchByEmail(String email) {
        Client client = clientRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with email: " + email));
        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientExistenceResponse existsById(Long id) {
        return ClientExistenceResponse.builder()
                .clientId(id)
                .exists(clientRepository.existsById(id))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientProfileResponse getClientProfile(Long id) {
        Client client = findClientById(id);
        List<ClientReservationResponse> reservations = fetchReservationsByClientId(id);

        return ClientProfileResponse.builder()
                .client(clientMapper.toResponse(client))
                .stayPreferences(clientMapper.toStayPreferencesResponse(client.getStayPreferences()))
                .loyalty(buildLoyaltyResponse(id, reservations))
                .historySummary(buildHistoryResponse(client, reservations, false))
                .recentReservations(reservations.stream()
                        .sorted(Comparator.comparing(ClientReservationResponse::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(5)
                        .toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientLoyaltyResponse getClientLoyalty(Long id) {
        findClientById(id);
        return buildLoyaltyResponse(id, fetchReservationsByClientId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientHistoryResponse getClientHistory(Long id) {
        Client client = findClientById(id);
        return buildHistoryResponse(client, fetchReservationsByClientId(id), true);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientStayPreferencesResponse getClientPreferences(Long id) {
        return clientMapper.toStayPreferencesResponse(findClientById(id).getStayPreferences());
    }

    @Override
    public ClientStayPreferencesResponse updateClientPreferences(Long id, ClientStayPreferencesRequest request) {
        Client client = findClientById(id);
        List<String> changes = collectPreferenceChanges(client.getStayPreferences(), request);

        client.setStayPreferences(clientMapper.toStayPreferencesEntity(request));
        Client updatedClient = clientRepository.save(client);

        if (!changes.isEmpty()) {
            recordChange(updatedClient.getId(), ClientChangeType.PREFERENCES_UPDATED,
                    "Updated stay preferences: " + String.join(", ", changes) + ".");
        }

        return clientMapper.toStayPreferencesResponse(updatedClient.getStayPreferences());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientChangeResponse> getClientChangeHistory(Long id) {
        findClientById(id);
        return clientChangeHistoryRepository.findByClientIdOrderByChangedAtDesc(id)
                .stream()
                .map(change -> ClientChangeResponse.builder()
                        .id(change.getId())
                        .clientId(change.getClientId())
                        .action(change.getAction())
                        .details(change.getDetails())
                        .changedBy(change.getChangedBy())
                        .changedAt(change.getChangedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientPageResponse getClientsPage(int page, int size, String sortBy, String direction,
                                             ClientPageSearchMode mode, String query) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        Sort.Direction safeDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        String trimmedQuery = query == null ? "" : query.trim();
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(safeDirection, safeSortBy));

        Page<Client> result = resolvePagedSearch(mode, trimmedQuery, pageable);

        return ClientPageResponse.builder()
                .content(result.getContent().stream().map(clientMapper::toResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .sortBy(safeSortBy)
                .direction(safeDirection.name().toLowerCase(Locale.ROOT))
                .mode(mode)
                .query(trimmedQuery)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientStatisticsResponse getClientStatistics() {
        List<Client> clients = clientRepository.findAll();
        List<ClientReservationResponse> reservations = fetchAllReservations();
        Map<Long, List<ClientReservationResponse>> reservationsByClient = reservations.stream()
                .collect(Collectors.groupingBy(ClientReservationResponse::getClientId));

        long totalClients = clients.size();
        long newClientsLast30Days = clientRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(30));
        long clientsWithPreferences = clients.stream().filter(client -> hasPreferences(client.getStayPreferences())).count();
        long clientsWithReservations = reservationsByClient.keySet().size();
        long confirmedReservations = reservations.stream().filter(reservation -> "CONFIRMED".equalsIgnoreCase(reservation.getStatus())).count();
        long cancelledReservations = reservations.stream().filter(reservation -> "CANCELLED".equalsIgnoreCase(reservation.getStatus())).count();
        double totalRevenue = reservations.stream()
                .filter(reservation -> !"CANCELLED".equalsIgnoreCase(reservation.getStatus()))
                .mapToDouble(ClientReservationResponse::getTotalPrice)
                .sum();

        Map<String, Long> loyaltyDistribution = new LinkedHashMap<>();
        Arrays.stream(ClientLoyaltyStatus.values()).forEach(status ->
                loyaltyDistribution.put(status.name(), clients.stream()
                        .filter(client -> buildLoyaltyResponse(client.getId(),
                                reservationsByClient.getOrDefault(client.getId(), List.of())).getStatus() == status)
                        .count())
        );

        return ClientStatisticsResponse.builder()
                .totalClients(totalClients)
                .newClientsLast30Days(newClientsLast30Days)
                .clientsWithPreferences(clientsWithPreferences)
                .clientsWithReservations(clientsWithReservations)
                .totalReservations(reservations.size())
                .confirmedReservations(confirmedReservations)
                .cancelledReservations(cancelledReservations)
                .totalRevenue(round(totalRevenue))
                .averageReservationsPerClient(totalClients == 0 ? 0 : round(reservations.size() / (double) totalClients))
                .averageRevenuePerClient(totalClients == 0 ? 0 : round(totalRevenue / totalClients))
                .loyaltyDistribution(loyaltyDistribution)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportClientsCsv() {
        List<Client> clients = clientRepository.findAll(Sort.by(Sort.Direction.ASC, "lastName").and(Sort.by("firstName")));
        Map<Long, List<ClientReservationResponse>> reservationsByClient = fetchAllReservations().stream()
                .collect(Collectors.groupingBy(ClientReservationResponse::getClientId));

        StringBuilder csv = new StringBuilder();
        csv.append("id,first_name,last_name,email,phone,address,national_id,created_at,updated_at,loyalty_status,loyalty_points,reservation_count,total_spent,preferred_room_type,preferred_bed_type,preferred_floor,quiet_room,non_smoking,high_floor,needs_baby_bed,special_requests\n");

        for (Client client : clients) {
            ClientStayPreferences preferences = normalizePreferences(client.getStayPreferences());
            ClientLoyaltyResponse loyalty = buildLoyaltyResponse(client.getId(),
                    reservationsByClient.getOrDefault(client.getId(), List.of()));

            csv.append(csvValue(client.getId()))
                    .append(csvValue(client.getFirstName()))
                    .append(csvValue(client.getLastName()))
                    .append(csvValue(client.getEmail()))
                    .append(csvValue(client.getPhone()))
                    .append(csvValue(client.getAddress()))
                    .append(csvValue(client.getNationalId()))
                    .append(csvValue(client.getCreatedAt()))
                    .append(csvValue(client.getUpdatedAt()))
                    .append(csvValue(loyalty.getStatus()))
                    .append(csvValue(loyalty.getPoints()))
                    .append(csvValue(loyalty.getReservationCount()))
                    .append(csvValue(loyalty.getTotalSpent()))
                    .append(csvValue(preferences.getPreferredRoomType()))
                    .append(csvValue(preferences.getPreferredBedType()))
                    .append(csvValue(preferences.getPreferredFloor()))
                    .append(csvValue(preferences.isQuietRoom()))
                    .append(csvValue(preferences.isNonSmoking()))
                    .append(csvValue(preferences.isHighFloor()))
                    .append(csvValue(preferences.isNeedsBabyBed()))
                    .append(csvValue(preferences.getSpecialRequests()))
                    .append('\n');
        }

        return csv.toString();
    }

    private Page<Client> resolvePagedSearch(ClientPageSearchMode mode, String query, Pageable pageable) {
        if (query.isBlank() || mode == ClientPageSearchMode.ALL) {
            return clientRepository.findAll(pageable);
        }

        return switch (mode) {
            case FIRSTNAME -> clientRepository.findByFirstNameContainingIgnoreCase(query, pageable);
            case LASTNAME -> clientRepository.findByLastNameContainingIgnoreCase(query, pageable);
            case EMAIL -> clientRepository.findByEmailContainingIgnoreCase(query, pageable);
            case ALL -> clientRepository.findAll(pageable);
        };
    }

    private Client findClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
    }

    private void validateEmailUniqueness(String email, Long currentClientId) {
        clientRepository.findByEmailIgnoreCase(email)
                .ifPresent(existingClient -> {
                    if (currentClientId == null || !existingClient.getId().equals(currentClientId)) {
                        throw new EmailAlreadyExistsException("Email already exists: " + email);
                    }
                });
    }

    private void recordChange(Long clientId, ClientChangeType action, String details) {
        clientChangeHistoryRepository.save(ClientChangeHistory.builder()
                .clientId(clientId)
                .action(action)
                .details(details)
                .changedBy(SYSTEM_USER)
                .build());
    }

    private List<ClientReservationResponse> fetchReservationsByClientId(Long clientId) {
        try {
            return reservationServiceClient.getReservationsByClientId(clientId);
        } catch (FeignException exception) {
            throw new RemoteServiceException("Reservation service is unavailable for the requested client history.");
        }
    }

    private List<ClientReservationResponse> fetchAllReservations() {
        try {
            return reservationServiceClient.getAllReservations();
        } catch (FeignException exception) {
            throw new RemoteServiceException("Reservation service is unavailable for client analytics.");
        }
    }

    private ClientLoyaltyResponse buildLoyaltyResponse(Long clientId, List<ClientReservationResponse> reservations) {
        long confirmed = reservations.stream().filter(reservation -> "CONFIRMED".equalsIgnoreCase(reservation.getStatus())).count();
        long created = reservations.stream().filter(reservation -> "CREATED".equalsIgnoreCase(reservation.getStatus())).count();
        long cancelled = reservations.stream().filter(reservation -> "CANCELLED".equalsIgnoreCase(reservation.getStatus())).count();
        double totalSpent = reservations.stream()
                .filter(reservation -> !"CANCELLED".equalsIgnoreCase(reservation.getStatus()))
                .mapToDouble(ClientReservationResponse::getTotalPrice)
                .sum();
        int points = (int) (confirmed * 150 + created * 60 - cancelled * 40 + Math.floor(totalSpent / 25.0));

        return ClientLoyaltyResponse.builder()
                .clientId(clientId)
                .status(resolveLoyaltyStatus(points))
                .points(Math.max(points, 0))
                .reservationCount(reservations.size())
                .confirmedReservations(confirmed)
                .createdReservations(created)
                .cancelledReservations(cancelled)
                .totalSpent(round(totalSpent))
                .averageSpent(reservations.isEmpty() ? 0 : round(totalSpent / reservations.size()))
                .build();
    }

    private ClientLoyaltyStatus resolveLoyaltyStatus(int points) {
        if (points >= 1200) {
            return ClientLoyaltyStatus.PLATINUM;
        }
        if (points >= 750) {
            return ClientLoyaltyStatus.GOLD;
        }
        if (points >= 400) {
            return ClientLoyaltyStatus.SILVER;
        }
        if (points >= 150) {
            return ClientLoyaltyStatus.BRONZE;
        }
        return ClientLoyaltyStatus.NEW;
    }

    private ClientHistoryResponse buildHistoryResponse(Client client, List<ClientReservationResponse> reservations,
                                                       boolean includeReservations) {
        List<ClientReservationResponse> sortedReservations = reservations.stream()
                .sorted(Comparator.comparing(ClientReservationResponse::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        long confirmed = reservations.stream().filter(reservation -> "CONFIRMED".equalsIgnoreCase(reservation.getStatus())).count();
        long created = reservations.stream().filter(reservation -> "CREATED".equalsIgnoreCase(reservation.getStatus())).count();
        long cancelled = reservations.stream().filter(reservation -> "CANCELLED".equalsIgnoreCase(reservation.getStatus())).count();
        double totalSpent = reservations.stream()
                .filter(reservation -> !"CANCELLED".equalsIgnoreCase(reservation.getStatus()))
                .mapToDouble(ClientReservationResponse::getTotalPrice)
                .sum();

        LocalDate firstReservationDate = reservations.stream()
                .map(ClientReservationResponse::getStartDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate lastReservationDate = reservations.stream()
                .map(ClientReservationResponse::getEndDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        LocalDate nextReservationDate = reservations.stream()
                .filter(reservation -> reservation.getStartDate() != null && !reservation.getStartDate().isBefore(LocalDate.now()))
                .map(ClientReservationResponse::getStartDate)
                .min(LocalDate::compareTo)
                .orElse(null);

        return ClientHistoryResponse.builder()
                .clientId(client.getId())
                .clientFullName(client.getFirstName() + " " + client.getLastName())
                .reservationCount(reservations.size())
                .confirmedReservations(confirmed)
                .createdReservations(created)
                .cancelledReservations(cancelled)
                .totalSpent(round(totalSpent))
                .averageSpent(reservations.isEmpty() ? 0 : round(totalSpent / reservations.size()))
                .firstReservationDate(firstReservationDate)
                .lastReservationDate(lastReservationDate)
                .nextReservationDate(nextReservationDate)
                .reservations(includeReservations ? sortedReservations : List.of())
                .build();
    }

    private List<String> collectProfileChanges(Client client, ClientRequest request) {
        List<String> changes = new ArrayList<>();

        if (!Objects.equals(client.getFirstName(), request.getFirstName())) {
            changes.add("first name");
        }
        if (!Objects.equals(client.getLastName(), request.getLastName())) {
            changes.add("last name");
        }
        if (!Objects.equals(client.getEmail(), request.getEmail())) {
            changes.add("email");
        }
        if (!Objects.equals(client.getPhone(), request.getPhone())) {
            changes.add("phone");
        }
        if (!Objects.equals(client.getAddress(), request.getAddress())) {
            changes.add("address");
        }
        if (!Objects.equals(client.getNationalId(), request.getNationalId())) {
            changes.add("national ID");
        }

        return changes;
    }

    private List<String> collectPreferenceChanges(ClientStayPreferences existingPreferences,
                                                  ClientStayPreferencesRequest request) {
        ClientStayPreferences current = normalizePreferences(existingPreferences);
        ClientStayPreferences incoming = normalizePreferences(clientMapper.toStayPreferencesEntity(request));
        List<String> changes = new ArrayList<>();

        if (!Objects.equals(current.getPreferredRoomType(), incoming.getPreferredRoomType())) {
            changes.add("preferred room type");
        }
        if (!Objects.equals(current.getPreferredBedType(), incoming.getPreferredBedType())) {
            changes.add("preferred bed type");
        }
        if (!Objects.equals(current.getPreferredFloor(), incoming.getPreferredFloor())) {
            changes.add("preferred floor");
        }
        if (current.isQuietRoom() != incoming.isQuietRoom()) {
            changes.add("quiet room");
        }
        if (current.isNonSmoking() != incoming.isNonSmoking()) {
            changes.add("non smoking");
        }
        if (current.isHighFloor() != incoming.isHighFloor()) {
            changes.add("high floor");
        }
        if (current.isNeedsBabyBed() != incoming.isNeedsBabyBed()) {
            changes.add("baby bed");
        }
        if (!Objects.equals(current.getSpecialRequests(), incoming.getSpecialRequests())) {
            changes.add("special requests");
        }

        return changes;
    }

    private ClientStayPreferences normalizePreferences(ClientStayPreferences preferences) {
        if (preferences != null) {
            return preferences;
        }

        return ClientStayPreferences.builder()
                .quietRoom(false)
                .nonSmoking(false)
                .highFloor(false)
                .needsBabyBed(false)
                .build();
    }

    private boolean hasPreferences(ClientStayPreferences preferences) {
        ClientStayPreferences current = normalizePreferences(preferences);
        return current.getPreferredRoomType() != null
                || current.getPreferredBedType() != null
                || current.getPreferredFloor() != null
                || current.getSpecialRequests() != null
                || current.isQuietRoom()
                || current.isNonSmoking()
                || current.isHighFloor()
                || current.isNeedsBabyBed();
    }

    private String csvValue(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\",";
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
