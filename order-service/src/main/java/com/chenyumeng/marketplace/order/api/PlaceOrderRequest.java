package com.chenyumeng.marketplace.order.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record PlaceOrderRequest(
        @NotBlank String sellerId,
        @NotBlank String buyerId,
        @Positive long amountCents,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency) {
}
