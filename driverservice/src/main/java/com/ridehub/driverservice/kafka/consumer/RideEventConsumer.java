package com.ridehub.driverservice.kafka.consumer;


import com.ridehub.driverservice.entity.Driver;
import com.ridehub.driverservice.enums.AvailabilityStatus;
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
                "Ride {} assigned to driver {}",
                event.getRideId(),
                event.getDriverId());

        Driver driver = driverRepository.findById(event.getDriverId())
                .orElseThrow();

        driver.setAvailability(AvailabilityStatus.ON_TRIP);
        driverRepository.save(driver);

        driverEventPublisher.publishDriverAvailabilityChanged(
                DriverAvailabilityChangedEvent.builder()
                        .driverId(driver.getId())
                        .userId(driver.getUserId())
                        .available(false)
                        .changedAt(LocalDateTime.now())
                        .build()
        );
    }

    @KafkaListener(
            topics = "ride-completed",
            groupId = "driver-service")
    public void consumeRideCompleted(
            RideCompletedEvent event) {

        log.info(
                "Ride {} completed",
                event.getRideId());

        Driver driver = driverRepository.findById(event.getDriverId())
                .orElseThrow();

        driver.setAvailability(AvailabilityStatus.ONLINE);
        driverRepository.save(driver);

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
                "Ride {} cancelled",
                event.getRideId());

        // optional depending on business rules
    }

    @KafkaListener(
            topics = "ride-started",
            groupId = "driver-service")
    public void consumeRideStarted(
            RideStartedEvent event) {

        log.info(
                "Ride {} started",
                event.getRideId());
    }

}
