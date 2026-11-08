package com.chenyumeng.marketplace.payout;

import com.chenyumeng.marketplace.events.OrderPlacedEvent;
import com.chenyumeng.marketplace.payout.domain.CommissionCalculator;
import com.chenyumeng.marketplace.payout.domain.CommissionCalculator.Split;
import com.chenyumeng.marketplace.payout.domain.PayoutEntry;
import com.chenyumeng.marketplace.payout.domain.PayoutEntryRepository;
import com.chenyumeng.marketplace.payout.domain.PayoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/** Étape 2 : ignoresADuplicateEvent fails until PayoutService.record is idempotent. */
@ExtendWith(MockitoExtension.class)
class PayoutServiceTest {

    private static final Instant NOW = Instant.parse("2027-01-04T10:00:00Z");

    @Mock
    private PayoutEntryRepository entries;

    @Mock
    private CommissionCalculator calculator;

    private PayoutService service;
    private OrderPlacedEvent event;

    @BeforeEach
    void setUp() {
        service = new PayoutService(entries, calculator, Clock.fixed(NOW, ZoneOffset.UTC));
        event = new OrderPlacedEvent(UUID.randomUUID(), "seller-1", "buyer-1", 2500, "EUR", NOW);
        lenient().when(calculator.split(2500)).thenReturn(new Split(250, 2250));
    }

    @Test
    void recordsAPayoutForANewOrder() {
        lenient().when(entries.existsByOrderId(event.orderId())).thenReturn(false);

        assertThat(service.record(event)).isTrue();

        ArgumentCaptor<PayoutEntry> saved = ArgumentCaptor.forClass(PayoutEntry.class);
        verify(entries).save(saved.capture());
        assertThat(saved.getValue().getOrderId()).isEqualTo(event.orderId());
        assertThat(saved.getValue().getCommissionCents()).isEqualTo(250);
        assertThat(saved.getValue().getNetCents()).isEqualTo(2250);
    }

    @Test
    void ignoresADuplicateEvent() {
        lenient().when(entries.existsByOrderId(event.orderId())).thenReturn(true);

        assertThat(service.record(event)).isFalse();

        verify(entries, never()).save(any());
    }
}
