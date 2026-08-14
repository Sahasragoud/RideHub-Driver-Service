package com.ridehub.driverservice.service.impl;

import com.ridehub.driverservice.dto.request.DriverAvailabilityUpdateRequest;
import com.ridehub.driverservice.dto.request.DriverRegistrationRequest;
import com.ridehub.driverservice.dto.request.DriverUpdateRequest;
import com.ridehub.driverservice.dto.response.DriverAvailabilityResponse;
import com.ridehub.driverservice.dto.response.DriverResponse;
import com.ridehub.driverservice.entity.Driver;
import com.ridehub.driverservice.enums.AvailabilityStatus;
import com.ridehub.driverservice.enums.DriverStatus;
import com.ridehub.driverservice.exception.DuplicateResourceException;
import com.ridehub.driverservice.exception.BusinessRuleViolationException;
import com.ridehub.driverservice.exception.ResourceNotFoundException;
import com.ridehub.driverservice.kafka.dto.DriverAvailabilityChangedEvent;
import com.ridehub.driverservice.kafka.dto.DriverRegisteredEvent;
import com.ridehub.driverservice.kafka.publisher.DriverEventPublisher;
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
    private final DriverEventPublisher driverEventPublisher;

    @Override
    public DriverResponse registerDriver(
            Long userId,
            DriverRegistrationRequest request) {

        log.info("Driver registration request received for userId: {}", userId);

        if (driverRepository.existsByUserId(userId)) {

            throw new DuplicateResourceException(
                    "Driver profile already exists.");
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException(
                    "License number already registered.");
        }

        if (driverRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new DuplicateResourceException(
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

        driverEventPublisher.publishDriverRegistered(

                DriverRegisteredEvent.builder()
                        .driverId(savedDriver.getId())
                        .userId(savedDriver.getUserId())
                        .vehicleNumber(savedDriver.getVehicleNumber())
                        .vehicleType(savedDriver.getVehicleType())
                        .available(savedDriver.getAvailability() == AvailabilityStatus.ONLINE)
                        .registeredAt(savedDriver.getCreatedAt())
                        .build()
        );

        log.info("Driver profile created successfully. Driver ID: {}",
                savedDriver.getId());

        return mapToResponse(savedDriver);
    }

    @Override
    public DriverResponse getDriverProfile(Long userId) {

        log.info("Fetching driver profile for userId: {}", userId);

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver profile not found."));

        log.info("Driver profile found. Driver ID: {}", driver.getId());

        return mapToResponse(driver);
    }

    @Override
    public DriverResponse updateDriver(
            Long userId,
            DriverUpdateRequest request) {

        log.info("Updating driver profile for userId: {}", userId);

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver profile not found."));

        // Vehicle number can be changed, but must remain unique
        if (!driver.getVehicleNumber().equals(request.getVehicleNumber())
                && driverRepository.existsByVehicleNumber(request.getVehicleNumber())) {

            throw new DuplicateResourceException(
                    "Vehicle number already registered.");
        }

        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setVehicleNumber(request.getVehicleNumber());
        driver.setVehicleType(request.getVehicleType());
        driver.setVehicleModel(request.getVehicleModel());
        driver.setVehicleColor(request.getVehicleColor());
        driver.setExperienceYears(request.getExperienceYears());

        Driver updatedDriver = driverRepository.save(driver);

        log.info("Driver profile updated successfully. Driver ID: {}",
                updatedDriver.getId());

        return mapToResponse(updatedDriver);
    }

    @Override
    public void deleteDriver(Long userId) {

        log.info("Deleting driver profile for userId: {}", userId);

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver profile not found."));

        driverRepository.delete(driver);

        log.info("Driver profile deleted successfully. Driver ID: {}",
                driver.getId());
    }

    @Override
    public DriverResponse updateAvailability(
            Long userId,
            DriverAvailabilityUpdateRequest request) {


        log.info("Availability update request for userId: {}", userId);

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver profile not found."));

        if (request.getAvailability() == AvailabilityStatus.ONLINE
                && driver.getStatus() != DriverStatus.APPROVED) {

            throw new BusinessRuleViolationException(
                    "Driver must be approved before going online.");
        }

        if (driver.getAvailability() == AvailabilityStatus.ON_TRIP &&
                request.getAvailability() == AvailabilityStatus.OFFLINE) {

            throw new BusinessRuleViolationException(
                    "Driver cannot go OFFLINE while on a trip.");
        }

        if (driver.getAvailability() == AvailabilityStatus.ON_TRIP &&
                request.getAvailability() == AvailabilityStatus.ONLINE) {

            throw new BusinessRuleViolationException(
                    "Driver is currently on a trip.");
        }

        if (driver.getAvailability() == request.getAvailability()) {

            throw new BusinessRuleViolationException(
                    "Driver is already " + request.getAvailability() + ".");
        }

        driver.setAvailability(request.getAvailability());

        Driver updatedDriver = driverRepository.save(driver);

        driverEventPublisher.publishDriverAvailabilityChanged(

                DriverAvailabilityChangedEvent.builder()
                        .driverId(updatedDriver.getId())
                        .userId(updatedDriver.getUserId())
                        .available(updatedDriver.getAvailability() == AvailabilityStatus.ONLINE)
                        .changedAt(java.time.LocalDateTime.now())
                        .build()
        );

        log.info(
                "Driver {} availability changed to {}",
                updatedDriver.getId(),
                updatedDriver.getAvailability());

        return mapToResponse(updatedDriver);
    }

    @Override
    public DriverAvailabilityResponse getAvailability(Long userId) {

        log.info("Fetching availability for userId: {}", userId);

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver profile not found."));

        return DriverAvailabilityResponse.builder()
                .availability(driver.getAvailability())
                .build();
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