package com.ridehub.driverservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.driverservice.entity.Driver;
import com.ridehub.driverservice.enums.AvailabilityStatus;
import com.ridehub.driverservice.exception.ResourceNotFoundException;
import com.ridehub.driverservice.kafka.dto.*;
import com.ridehub.driverservice.kafka.publisher.DriverEventPublisher;
import com.ridehub.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RideEventConsumer {

    private final DriverRepository driverRepository;
    private final DriverEventPublisher driverEventPublisher;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "ride-assigned",
            groupId = "driver-service")
    public void consumeRideAssigned(String message) {

        try {
            RideAssignedEvent event =
                    objectMapper.readValue(message, RideAssignedEvent.class);

            log.info(
                    "Received RideAssignedEvent. Ride={}, Driver={}",
                    event.getRideId(),
                    event.getDriverId()
            );

            Driver driver = driverRepository
                    .findByUserId(event.getDriverId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Driver not found."));

            if (driver.getAvailability() != AvailabilityStatus.ON_TRIP) {

                driver.setAvailability(AvailabilityStatus.ON_TRIP);
                driverRepository.save(driver);

                driverEventPublisher.publishDriverBusyEvent(
                        DriverBusyEvent.builder()
                                .driverId(driver.getId())
                                .userId(driver.getUserId())
                                .rideId(event.getRideId())
                                .busyAt(LocalDateTime.now())
                                .build()
                );

                driverEventPublisher.publishDriverAvailabilityChanged(
                        DriverAvailabilityChangedEvent.builder()
                                .driverId(driver.getId())
                                .userId(driver.getUserId())
                                .available(false)
                                .changedAt(LocalDateTime.now())
                                .build()
                );
            }

        } catch (Exception e) {
            log.error("Failed to process ride-assigned event: {}", message, e);
        }
    }

    @KafkaListener(
            topics = "ride-completed",
            groupId = "driver-service")
    public void consumeRideCompleted(String message) {

        try {
            RideCompletedEvent event =
                    objectMapper.readValue(message, RideCompletedEvent.class);

            log.info(
                    "Received RideCompletedEvent. Ride={}, Driver={}",
                    event.getRideId(),
                    event.getDriverId()
            );

            Driver driver = driverRepository
                    .findByUserId(event.getDriverId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Driver not found."));

            driver.setAvailability(AvailabilityStatus.ONLINE);
            driverRepository.save(driver);

            driverEventPublisher.publishDriverAvailableEvent(
                    DriverAvailableEvent.builder()
                            .driverId(driver.getId())
                            .userId(driver.getUserId())
                            .availableAt(LocalDateTime.now())
                            .build()
            );

            driverEventPublisher.publishDriverAvailabilityChanged(
                    DriverAvailabilityChangedEvent.builder()
                            .driverId(driver.getId())
                            .userId(driver.getUserId())
                            .available(true)
                            .changedAt(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to process ride-completed event: {}", message, e);
        }
    }

    @KafkaListener(
            topics = "ride-cancelled",
            groupId = "driver-service")
    public void consumeRideCancelled(String message) {

        try {
            RideCancelledEvent event =
                    objectMapper.readValue(message, RideCancelledEvent.class);

            log.info(
                    "Received RideCancelledEvent. Ride={}, Driver={}",
                    event.getRideId(),
                    event.getDriverId()
            );

            Driver driver = driverRepository
                    .findByUserId(event.getDriverId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Driver not found."));

            if (driver.getAvailability() == AvailabilityStatus.ON_TRIP) {

                driver.setAvailability(AvailabilityStatus.ONLINE);
                driverRepository.save(driver);

                driverEventPublisher.publishDriverAvailableEvent(
                        DriverAvailableEvent.builder()
                                .driverId(driver.getId())
                                .userId(driver.getUserId())
                                .availableAt(LocalDateTime.now())
                                .build()
                );

                driverEventPublisher.publishDriverAvailabilityChanged(
                        DriverAvailabilityChangedEvent.builder()
                                .driverId(driver.getId())
                                .userId(driver.getUserId())
                                .available(true)
                                .changedAt(LocalDateTime.now())
                                .build()
                );
            }

        } catch (Exception e) {
            log.error("Failed to process ride-cancelled event: {}", message, e);
        }
    }

    @KafkaListener(
            topics = "ride-started",
            groupId = "driver-service")
    public void consumeRideStarted(String message) {

        try {
            RideStartedEvent event =
                    objectMapper.readValue(message, RideStartedEvent.class);

            log.info(
                    "Received RideStartedEvent. Ride={}, Driver={}",
                    event.getRideId(),
                    event.getDriverId()
            );

        } catch (Exception e) {
            log.error("Failed to process ride-started event: {}", message, e);
        }
    }
}