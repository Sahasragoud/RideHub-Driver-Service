package com.ridehub.driverservice.kafka.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideAssignedEvent {

    private Long rideId;

    private Long driverId;

    private LocalDateTime assignedAt;

}
