package com.chenyumeng.marketplace.payout;

import com.chenyumeng.marketplace.payout.domain.CommissionCalculator;
import com.chenyumeng.marketplace.payout.domain.CommissionCalculator.Split;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Étape 1 : these tests fail until CommissionCalculator.split is implemented. */
class CommissionCalculatorTest {

    private final CommissionCalculator calculator = new CommissionCalculator(new BigDecimal("0.10"));

    @Test
    void keepsTenPercentAsCommission() {
        assertThat(calculator.split(10_000)).isEqualTo(new Split(1_000, 9_000));
    }

    @Test
    void roundsTheCommissionHalfUpToTheCent() {
        assertThat(calculator.split(999)).isEqualTo(new Split(100, 899)); // 99.9 -> 100
        assertThat(calculator.split(5)).isEqualTo(new Split(1, 4));       // 0.5  -> 1
        assertThat(calculator.split(4)).isEqualTo(new Split(0, 4));       // 0.4  -> 0
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 7, 999, 123_457, 10_000_000})
    void neverLosesACent(long grossCents) {
        Split split = calculator.split(grossCents);
        assertThat(split.commissionCents() + split.netCents()).isEqualTo(grossCents);
    }

    @Test
    void rejectsNonPositiveAmounts() {
        assertThatThrownBy(() -> calculator.split(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> calculator.split(-100)).isInstanceOf(IllegalArgumentException.class);
    }
}
