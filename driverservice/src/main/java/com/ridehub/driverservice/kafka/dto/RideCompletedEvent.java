package com.ridehub.driverservice.kafka.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideCompletedEvent {

    private Long rideId;

    private Long riderId;

    private Long driverId;

    private BigDecimal fare;

    private LocalDateTime completedAt;

}
