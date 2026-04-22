package com.hotel.clientservice.service;

import com.hotel.clientservice.dto.ClientExistenceResponse;
import com.hotel.clientservice.dto.ClientChangeResponse;
import com.hotel.clientservice.dto.ClientHistoryResponse;
import com.hotel.clientservice.dto.ClientLoyaltyResponse;
import com.hotel.clientservice.dto.ClientPageResponse;
import com.hotel.clientservice.dto.ClientPageSearchMode;
import com.hotel.clientservice.dto.ClientProfileResponse;
import com.hotel.clientservice.dto.ClientRequest;
import com.hotel.clientservice.dto.ClientResponse;
import com.hotel.clientservice.dto.ClientStatisticsResponse;
import com.hotel.clientservice.dto.ClientStayPreferencesRequest;
import com.hotel.clientservice.dto.ClientStayPreferencesResponse;

import java.util.List;

public interface ClientService {

    ClientResponse createClient(ClientRequest request);

    List<ClientResponse> getAllClients();

    ClientResponse getClientById(Long id);

    ClientResponse updateClient(Long id, ClientRequest request);

    void deleteClient(Long id);

    List<ClientResponse> searchByFirstName(String firstName);

    List<ClientResponse> searchByLastName(String lastName);

    ClientResponse searchByEmail(String email);

    ClientExistenceResponse existsById(Long id);

    ClientProfileResponse getClientProfile(Long id);

    ClientLoyaltyResponse getClientLoyalty(Long id);

    ClientHistoryResponse getClientHistory(Long id);

    ClientStayPreferencesResponse getClientPreferences(Long id);

    ClientStayPreferencesResponse updateClientPreferences(Long id, ClientStayPreferencesRequest request);

    List<ClientChangeResponse> getClientChangeHistory(Long id);

    ClientPageResponse getClientsPage(int page, int size, String sortBy, String direction, ClientPageSearchMode mode, String query);

    ClientStatisticsResponse getClientStatistics();

    String exportClientsCsv();
}
