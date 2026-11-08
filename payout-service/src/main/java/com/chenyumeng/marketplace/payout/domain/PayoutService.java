package com.chenyumeng.marketplace.payout.domain;

import com.chenyumeng.marketplace.events.OrderPlacedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class PayoutService {

    private final PayoutEntryRepository entries;
    private final CommissionCalculator calculator;
    private final Clock clock;

    public PayoutService(PayoutEntryRepository entries, CommissionCalculator calculator, Clock clock) {
        this.entries = entries;
        this.calculator = calculator;
        this.clock = clock;
    }

    /**
     * Records the seller's payout for an order.
     *
     * @return true if a new payout was recorded, false if this order had already been paid
     */
    @Transactional
    public boolean record(OrderPlacedEvent event) {
        if (entries.existsByOrderId(event.orderId())) {
            return false;
        }

        CommissionCalculator.Split split = calculator.split(event.amountCents());
        entries.save(new PayoutEntry(UUID.randomUUID(), event.orderId(), event.sellerId(),
                event.amountCents(), split.commissionCents(), split.netCents(),
                event.currency(), clock.instant()));
        return true;
    }

    @Transactional(readOnly = true)
    public long balanceOf(String sellerId) {
        return entries.sumNetCentsBySeller(sellerId);
    }
}
