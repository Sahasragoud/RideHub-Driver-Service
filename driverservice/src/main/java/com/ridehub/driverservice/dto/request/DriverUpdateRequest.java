package com.ridehub.driverservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverUpdateRequest {

    @Future(message = "License expiry must be a future date")
    private LocalDate licenseExpiry;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;

    @NotBlank(message = "Vehicle color is required")
    private String vehicleColor;

    @Min(value = 0)
    @Max(value = 50)
    private Integer experienceYears;
}