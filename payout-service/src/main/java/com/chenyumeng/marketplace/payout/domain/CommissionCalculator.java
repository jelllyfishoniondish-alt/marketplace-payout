package com.chenyumeng.marketplace.payout.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Splits the amount paid by the buyer into the marketplace commission and the seller's net share. */
@Component
public class CommissionCalculator {

    public record Split(long commissionCents, long netCents) {
    }

    private final BigDecimal rate;

    public CommissionCalculator(@Value("${app.commission-rate}") BigDecimal rate) {
        this.rate = rate;
    }

    public Split split(long grossCents) {
        if (grossCents <= 0) {
            throw new IllegalArgumentException("grossCents must be positive, got: " + grossCents);
        }
        long commissionCents = BigDecimal.valueOf(grossCents)
                .multiply(rate)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
        return new Split(commissionCents, grossCents - commissionCents);
    }
}
