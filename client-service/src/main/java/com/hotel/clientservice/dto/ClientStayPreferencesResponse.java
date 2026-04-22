package com.hotel.clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientStayPreferencesResponse {

    private String preferredRoomType;
    private String preferredBedType;
    private Integer preferredFloor;
    private boolean quietRoom;
    private boolean nonSmoking;
    private boolean highFloor;
    private boolean needsBabyBed;
    private String specialRequests;
}
