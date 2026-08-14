package com.ridehub.driverservice.kafka.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverAvailableEvent {

    private Long driverId;

    private Long rideId;

    private Long userId;

    private LocalDateTime availableAt;
}