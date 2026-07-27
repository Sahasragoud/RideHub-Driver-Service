package com.ridehub.driverservice.kafka.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegisteredEvent {

    private Long driverId;

    private Long userId;

    private String vehicleNumber;

    private String vehicleType;

    private boolean available;

    private LocalDateTime registeredAt;
}