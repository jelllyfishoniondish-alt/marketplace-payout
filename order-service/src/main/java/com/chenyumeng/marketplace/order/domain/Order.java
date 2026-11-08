package com.chenyumeng.marketplace.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "CustomerOrder") // "Order" is also a JPQL keyword (ORDER BY)
@Table(name = "orders") // "order" is a reserved SQL keyword
public class Order {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private String buyerId;

    @Column(nullable = false)
    private long amountCents;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected Order() {
        // required by JPA
    }

    public Order(UUID id, String sellerId, String buyerId, long amountCents, String currency, Instant createdAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.amountCents = amountCents;
        this.currency = currency;
        this.createdAt = createdAt;
        this.status = OrderStatus.PLACED;
    }

    public UUID getId() { return id; }
    public String getSellerId() { return sellerId; }
    public String getBuyerId() { return buyerId; }
    public long getAmountCents() { return amountCents; }
    public String getCurrency() { return currency; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
