package com.chenyumeng.marketplace.order;

import com.chenyumeng.marketplace.events.OrderPlacedEvent;
import com.chenyumeng.marketplace.order.api.PlaceOrderRequest;
import com.chenyumeng.marketplace.order.domain.Order;
import com.chenyumeng.marketplace.order.domain.OrderRepository;
import com.chenyumeng.marketplace.order.domain.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/** Plain unit test: no Spring context, no database, no Kafka. */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Instant NOW = Instant.parse("2027-01-04T10:00:00Z");

    @Mock
    private OrderRepository orders;

    @Mock
    private KafkaTemplate<String, OrderPlacedEvent> kafka;

    @Test
    void placingAnOrderSavesItAndPublishesAnEventKeyedBySeller() {
        OrderService service = new OrderService(orders, kafka, "orders.placed",
                Clock.fixed(NOW, ZoneOffset.UTC));

        Order order = service.place(new PlaceOrderRequest("seller-1", "buyer-1", 2500, "EUR"));

        verify(orders).save(order);
        ArgumentCaptor<OrderPlacedEvent> event = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(kafka).send(eq("orders.placed"), eq("seller-1"), event.capture());
        assertThat(event.getValue().orderId()).isEqualTo(order.getId());
        assertThat(event.getValue().amountCents()).isEqualTo(2500);
        assertThat(event.getValue().placedAt()).isEqualTo(NOW);
    }
}
