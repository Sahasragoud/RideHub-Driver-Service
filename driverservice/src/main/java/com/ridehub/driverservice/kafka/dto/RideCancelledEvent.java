package com.ridehub.driverservice.kafka.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideCancelledEvent {

    private Long rideId;

    private String reason;

    private LocalDateTime cancelledAt;

}
