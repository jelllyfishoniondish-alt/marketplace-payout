package com.chenyumeng.marketplace.order.api;

import com.chenyumeng.marketplace.order.domain.Order;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String sellerId,
        String buyerId,
        long amountCents,
        String currency,
        String status,
        Instant createdAt) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getSellerId(), order.getBuyerId(),
                order.getAmountCents(), order.getCurrency(), order.getStatus().name(), order.getCreatedAt());
    }
}
