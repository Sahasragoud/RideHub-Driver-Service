package com.ridehub.driverservice.dto.request;

import com.ridehub.driverservice.enums.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverAvailabilityUpdateRequest {

    @NotNull(message = "Availability is required.")
    private AvailabilityStatus availability;

}