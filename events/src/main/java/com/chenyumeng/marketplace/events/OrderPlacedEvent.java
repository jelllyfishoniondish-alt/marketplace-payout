package com.chenyumeng.marketplace.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Published by order-service on topic "orders.placed" when a buyer places an order.
 * Amounts are integer cents to avoid floating-point rounding errors.
 */
public record OrderPlacedEvent(
        UUID orderId,
        String sellerId,
        String buyerId,
        long amountCents,
        String currency,
        Instant placedAt) {
}
