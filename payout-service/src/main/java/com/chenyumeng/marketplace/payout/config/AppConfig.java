package com.chenyumeng.marketplace.payout.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Clock;

@Configuration
public class AppConfig {

    /** Declared here too, so the service can start even if order-service has not created the topic yet. */
    @Bean
    public NewTopic orderPlacedTopic(@Value("${app.topics.order-placed}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
