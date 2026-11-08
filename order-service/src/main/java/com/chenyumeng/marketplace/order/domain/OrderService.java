package com.chenyumeng.marketplace.order.domain;

import com.chenyumeng.marketplace.events.OrderPlacedEvent;
import com.chenyumeng.marketplace.order.api.PlaceOrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final KafkaTemplate<String, OrderPlacedEvent> kafka;
    private final String orderPlacedTopic;
    private final Clock clock;

    public OrderService(OrderRepository orders,
                        KafkaTemplate<String, OrderPlacedEvent> kafka,
                        @Value("${app.topics.order-placed}") String orderPlacedTopic,
                        Clock clock) {
        this.orders = orders;
        this.kafka = kafka;
        this.orderPlacedTopic = orderPlacedTopic;
        this.clock = clock;
    }

    @Transactional
    public Order place(PlaceOrderRequest request) {
        Order order = new Order(UUID.randomUUID(), request.sellerId(), request.buyerId(),
                request.amountCents(), request.currency(), clock.instant());
        orders.save(order);

        OrderPlacedEvent event = new OrderPlacedEvent(order.getId(), order.getSellerId(), order.getBuyerId(),
                order.getAmountCents(), order.getCurrency(), order.getCreatedAt());

        // Key = sellerId: all events of one seller go to the same partition, so they are processed in order.
        // Known limitation ("dual write"): the event is sent before the DB transaction commits.
        // If the commit then fails, a "ghost" event exists. Fix: transactional outbox (see ROADMAP.md).
        kafka.send(orderPlacedTopic, order.getSellerId(), event);
        return order;
    }

    @Transactional(readOnly = true)
    public Optional<Order> find(UUID id) {
        return orders.findById(id);
    }
}
