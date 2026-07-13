package com.ridehub.driverservice.service.impl;

import com.ridehub.driverservice.dto.request.DriverRegistrationRequest;
import com.ridehub.driverservice.dto.request.DriverUpdateRequest;
import com.ridehub.driverservice.dto.response.DriverResponse;
import com.ridehub.driverservice.entity.Driver;
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

        log.info("Driver registration request received for userId: {}", userId);

        if (driverRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException(
                    "Driver profile already exists.");
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new IllegalArgumentException(
                    "License number already registered.");
        }

        if (driverRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new IllegalArgumentException(
                    "Vehicle number already registered.");
        }

        Driver driver = Driver.builder()
                .userId(userId)
                .licenseNumber(request.getLicenseNumber())
                .licenseExpiry(request.getLicenseExpiry())
                .vehicleNumber(request.getVehicleNumber())
                .vehicleType(request.getVehicleType())
                .vehicleModel(request.getVehicleModel())
                .vehicleColor(request.getVehicleColor())
                .experienceYears(request.getExperienceYears())
                .build();

        Driver savedDriver = driverRepository.save(driver);

        log.info("Driver profile created successfully. Driver ID: {}",
                savedDriver.getId());

        return mapToResponse(savedDriver);
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


    private DriverResponse mapToResponse(Driver driver) {

        return DriverResponse.builder()
                .id(driver.getId())
                .userId(driver.getUserId())
                .licenseNumber(driver.getLicenseNumber())
                .licenseExpiry(driver.getLicenseExpiry())
                .vehicleNumber(driver.getVehicleNumber())
                .vehicleType(driver.getVehicleType())
                .vehicleModel(driver.getVehicleModel())
                .vehicleColor(driver.getVehicleColor())
                .experienceYears(driver.getExperienceYears())
                .status(driver.getStatus())
                .availability(driver.getAvailability())
                .rating(driver.getRating())
                .totalTrips(driver.getTotalTrips())
                .build();
    }
}