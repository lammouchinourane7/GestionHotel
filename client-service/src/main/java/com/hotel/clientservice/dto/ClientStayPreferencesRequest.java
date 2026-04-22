package com.hotel.clientservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientStayPreferencesRequest {

    private String preferredRoomType;
    private String preferredBedType;

    @Min(value = 0, message = "Preferred floor must be positive")
    @Max(value = 80, message = "Preferred floor is too high")
    private Integer preferredFloor;

    private boolean quietRoom;
    private boolean nonSmoking;
    private boolean highFloor;
    private boolean needsBabyBed;
    private String specialRequests;
}
