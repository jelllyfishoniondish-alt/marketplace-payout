package com.chenyumeng.marketplace.payout;

import com.chenyumeng.marketplace.events.OrderPlacedEvent;
import com.chenyumeng.marketplace.payout.domain.PayoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Real Kafka + real PostgreSQL (Testcontainers). Requires Docker.
 * Needs étape 1. Without étape 2 it still passes thanks to the unique constraint on orderId,
 * but the logs show exceptions and 10 retries: check them before and after étape 2.
 */
@SpringBootTest
@Testcontainers
class PayoutFlowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Autowired
    private PayoutService payoutService;

    @Value("${app.topics.order-placed}")
    private String topic;

    @Test
    void theSameEventDeliveredTwiceIsPaidOnlyOnce() {
        OrderPlacedEvent event = new OrderPlacedEvent(UUID.randomUUID(), "seller-42", "buyer-1",
                10_000, "EUR", Instant.now());

        kafkaTemplate.send(topic, event.sellerId(), event);
        kafkaTemplate.send(topic, event.sellerId(), event); // simulated redelivery

        await().atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> assertThat(payoutService.balanceOf("seller-42")).isEqualTo(9_000));

        // ...and it stays at 9 000: the duplicate was not paid a second time
        await().during(Duration.ofSeconds(3)).atMost(Duration.ofSeconds(10))
                .until(() -> payoutService.balanceOf("seller-42") == 9_000);
    }
}
