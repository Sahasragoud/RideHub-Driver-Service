package com.ridehub.driverservice.service.impl;

import com.ridehub.driverservice.dto.request.DriverRegistrationRequest;
import com.ridehub.driverservice.dto.request.DriverUpdateRequest;
import com.ridehub.driverservice.dto.response.DriverResponse;
import com.ridehub.driverservice.repository.DriverRepository;
import com.ridehub.driverservice.service.interfaces.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public DriverResponse registerDriver(
            Long userId,
            DriverRegistrationRequest request) {

        return null;
    }

    @Override
    public DriverResponse getDriverProfile(Long userId) {

        return null;
    }

    @Override
    public DriverResponse updateDriver(
            Long userId,
            DriverUpdateRequest request) {

        return null;
    }

    @Override
    public void deleteDriver(Long userId) {

    }
}