package com.hotel.clientservice.mapper;

import com.hotel.clientservice.dto.ClientRequest;
import com.hotel.clientservice.dto.ClientResponse;
import com.hotel.clientservice.dto.ClientStayPreferencesRequest;
import com.hotel.clientservice.dto.ClientStayPreferencesResponse;
import com.hotel.clientservice.entity.Client;
import com.hotel.clientservice.entity.ClientStayPreferences;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequest request) {
        return Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .nationalId(request.getNationalId())
                .stayPreferences(toStayPreferencesEntity(request.getStayPreferences()))
                .build();
    }

    public void updateEntity(Client client, ClientRequest request) {
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setNationalId(request.getNationalId());
        if (request.getStayPreferences() != null) {
            client.setStayPreferences(toStayPreferencesEntity(request.getStayPreferences()));
        }
    }

    public ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .nationalId(client.getNationalId())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .stayPreferences(toStayPreferencesResponse(client.getStayPreferences()))
                .build();
    }

    public ClientStayPreferencesResponse toStayPreferencesResponse(ClientStayPreferences preferences) {
        if (preferences == null) {
            return ClientStayPreferencesResponse.builder().build();
        }

        return ClientStayPreferencesResponse.builder()
                .preferredRoomType(preferences.getPreferredRoomType())
                .preferredBedType(preferences.getPreferredBedType())
                .preferredFloor(preferences.getPreferredFloor())
                .quietRoom(preferences.isQuietRoom())
                .nonSmoking(preferences.isNonSmoking())
                .highFloor(preferences.isHighFloor())
                .needsBabyBed(preferences.isNeedsBabyBed())
                .specialRequests(preferences.getSpecialRequests())
                .build();
    }

    public ClientStayPreferences toStayPreferencesEntity(ClientStayPreferencesRequest request) {
        if (request == null) {
            return null;
        }

        return ClientStayPreferences.builder()
                .preferredRoomType(request.getPreferredRoomType())
                .preferredBedType(request.getPreferredBedType())
                .preferredFloor(request.getPreferredFloor())
                .quietRoom(request.isQuietRoom())
                .nonSmoking(request.isNonSmoking())
                .highFloor(request.isHighFloor())
                .needsBabyBed(request.isNeedsBabyBed())
                .specialRequests(request.getSpecialRequests())
                .build();
    }
}
