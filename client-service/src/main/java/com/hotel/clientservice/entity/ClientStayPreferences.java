package com.hotel.clientservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientStayPreferences {

    @Column(name = "stay_preferred_room_type")
    private String preferredRoomType;

    @Column(name = "stay_preferred_bed_type")
    private String preferredBedType;

    @Column(name = "stay_preferred_floor")
    private Integer preferredFloor;

    @Column(name = "stay_quiet_room")
    private boolean quietRoom;

    @Column(name = "stay_non_smoking")
    private boolean nonSmoking;

    @Column(name = "stay_high_floor")
    private boolean highFloor;

    @Column(name = "stay_needs_baby_bed")
    private boolean needsBabyBed;

    @Column(name = "stay_special_requests", length = 500)
    private String specialRequests;
}
