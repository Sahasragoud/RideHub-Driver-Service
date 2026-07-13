package com.ridehub.driverservice.dto.response;

import com.ridehub.driverservice.enums.AvailabilityStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DriverAvailabilityResponse {

    private AvailabilityStatus availability;

}