package com.chenyumeng.marketplace.payout.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PayoutEntryRepository extends JpaRepository<PayoutEntry, UUID> {

    boolean existsByOrderId(UUID orderId);

    @Query("select coalesce(sum(p.netCents), 0L) from PayoutEntry p where p.sellerId = :sellerId")
    long sumNetCentsBySeller(@Param("sellerId") String sellerId);
}
