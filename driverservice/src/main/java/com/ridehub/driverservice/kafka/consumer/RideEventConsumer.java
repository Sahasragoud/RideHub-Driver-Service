package com.ridehub.driverservice.kafka.consumer;


import com.ridehub.driverservice.entity.Driver;
import com.ridehub.driverservice.enums.AvailabilityStatus;
import com.ridehub.driverservice.exception.ResourceNotFoundException;
import com.ridehub.driverservice.kafka.dto.*;
import com.ridehub.driverservice.kafka.publisher.DriverEventPublisher;
import com.ridehub.driverservice.repository.DriverRepository;
import lombok.*;
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

    @KafkaListener(
            topics = "ride-assigned",
            groupId = "driver-service")
    public void consumeRideAssigned(
            RideAssignedEvent event) {

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
    }

    @KafkaListener(
            topics = "ride-completed",
            groupId = "driver-service")
    public void consumeRideCompleted(
            RideCompletedEvent event) {

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
    }

    @KafkaListener(
            topics = "ride-cancelled",
            groupId = "driver-service")
    public void consumeRideCancelled(
            RideCancelledEvent event) {

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
    }

    @KafkaListener(
            topics = "ride-started",
            groupId = "driver-service")
    public void consumeRideStarted(
            RideStartedEvent event) {

        log.info(
                "Received RideStartedEvent. Ride={}, Driver={}",
                event.getRideId(),
                event.getDriverId()
        );
    }

}
