package com.chenyumeng.marketplace.payout.messaging;

import com.chenyumeng.marketplace.events.OrderPlacedEvent;
import com.chenyumeng.marketplace.payout.domain.PayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacedListener.class);

    private final PayoutService payoutService;

    public OrderPlacedListener(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @KafkaListener(topics = "${app.topics.order-placed}")
    public void onOrderPlaced(OrderPlacedEvent event) {
        boolean recorded = payoutService.record(event);
        if (recorded) {
            log.info("Payout recorded for order {} (seller {})", event.orderId(), event.sellerId());
        } else {
            log.info("Duplicate event ignored for order {}", event.orderId());
        }
    }
}
