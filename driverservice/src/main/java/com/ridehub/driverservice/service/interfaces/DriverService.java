package com.ridehub.driverservice.service.interfaces;

import com.ridehub.driverservice.dto.request.DriverAvailabilityUpdateRequest;
import com.ridehub.driverservice.dto.request.DriverRegistrationRequest;
import com.ridehub.driverservice.dto.request.DriverUpdateRequest;
import com.ridehub.driverservice.dto.response.DriverResponse;

public interface DriverService {

    DriverResponse registerDriver(
            Long userId,
            DriverRegistrationRequest request);

    DriverResponse getDriverProfile(Long userId);

    DriverResponse updateDriver(
            Long userId,
            DriverUpdateRequest request);

    void deleteDriver(Long userId);

    DriverResponse updateAvailability(
            Long userId,
            DriverAvailabilityUpdateRequest request);

}