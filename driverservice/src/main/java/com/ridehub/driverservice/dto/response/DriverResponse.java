package com.ridehub.driverservice.dto.response;

import com.ridehub.driverservice.enums.AvailabilityStatus;
import com.ridehub.driverservice.enums.DriverStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponse {

    private Long id;

    private Long userId;

    private String licenseNumber;

    private LocalDate licenseExpiry;

    private String vehicleNumber;

    private String vehicleType;

    private String vehicleModel;

    private String vehicleColor;

    private Integer experienceYears;

    private DriverStatus status;

    private AvailabilityStatus availability;

    private Double rating;

    private Integer totalTrips;
}