package com.chenyumeng.marketplace.payout.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** One line of the seller ledger: what the seller earns for one order. */
@Entity
@Table(name = "payout_entries")
public class PayoutEntry {

    @Id
    private UUID id;

    /** Unique: the database itself refuses to pay the same order twice (last line of defence). */
    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private long grossCents;

    @Column(nullable = false)
    private long commissionCents;

    @Column(nullable = false)
    private long netCents;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Instant createdAt;

    protected PayoutEntry() {
        // required by JPA
    }

    public PayoutEntry(UUID id, UUID orderId, String sellerId, long grossCents, long commissionCents,
                       long netCents, String currency, Instant createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.grossCents = grossCents;
        this.commissionCents = commissionCents;
        this.netCents = netCents;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public String getSellerId() { return sellerId; }
    public long getGrossCents() { return grossCents; }
    public long getCommissionCents() { return commissionCents; }
    public long getNetCents() { return netCents; }
    public String getCurrency() { return currency; }
    public Instant getCreatedAt() { return createdAt; }
}
