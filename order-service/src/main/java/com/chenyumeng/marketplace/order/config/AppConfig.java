package com.chenyumeng.marketplace.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Clock;

@Configuration
public class AppConfig {

    /** Created at startup by Spring's KafkaAdmin if it does not exist yet. */
    @Bean
    public NewTopic orderPlacedTopic(@Value("${app.topics.order-placed}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    /** Injected instead of calling Instant.now() so tests can use a fixed clock. */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
