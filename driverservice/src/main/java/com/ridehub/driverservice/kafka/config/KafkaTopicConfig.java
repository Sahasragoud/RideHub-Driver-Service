package com.ridehub.driverservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic driverRegisteredTopic() {
        return new NewTopic("driver-registered", 1, (short) 1);
    }

    @Bean
    public NewTopic driverAvailabilityChangedTopic() {
        return new NewTopic("driver-availability-changed", 1, (short) 1);
    }

    @Bean
    public NewTopic driverBusyTopic() {
        return new NewTopic("driver-busy", 1, (short) 1);
    }

    @Bean
    public NewTopic driverAvailableTopic() {
        return new NewTopic("driver-available", 1, (short) 1);
    }

}
