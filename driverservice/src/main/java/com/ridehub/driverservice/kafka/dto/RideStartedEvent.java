package com.ridehub.driverservice.kafka.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideStartedEvent {

    private Long rideId;

    private Long driverId;

    private LocalDateTime startedAt;

}