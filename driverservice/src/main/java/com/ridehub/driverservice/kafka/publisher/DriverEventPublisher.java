package com.ridehub.driverservice.kafka.publisher;

import com.ridehub.driverservice.kafka.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDriverRegistered(
            DriverRegisteredEvent event
    ){
        kafkaTemplate.send(
                "driver-registered",
                event.getDriverId().toString(),
                event
        );

        log.info(
                "Published DriverRegisteredEvent for driver {}",
                event.getDriverId()
        );
    }

    public void publishDriverAvailabilityChanged(
            DriverAvailabilityChangedEvent event
    ){
        kafkaTemplate.send(
                "driver-availability-changed",
                event.getDriverId().toString(),
                event
        );

        log.info(
                "Published DriverAvailabilityChangedEvent for driver {}",
                event.getDriverId()
        );
    }

    public void publishDriverAvailableEvent(
            DriverAvailableEvent event
    ){
        kafkaTemplate.send(
                "driver-available",
                event.getDriverId().toString(),
                event
        );

        log.info(
                "Published DriverAvailableEvent for driver {}",
                event.getDriverId()
        );
    }

    public void publishDriverBusyEvent(
            DriverBusyEvent event
    ){
        kafkaTemplate.send(
                "driver-busy",
                event.getDriverId().toString(),
                event
        );

        log.info(
                "Published DriverBusyEvent for driver {}",
                event.getDriverId()
        );
    }
}