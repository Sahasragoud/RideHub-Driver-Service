package com.ridehub.driverservice.kafka.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverAvailabilityChangedEvent {

    private Long driverId;

    private Long userId;

    private boolean available;

    private LocalDateTime changedAt;
}