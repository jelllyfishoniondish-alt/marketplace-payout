package com.chenyumeng.marketplace.payout.api;

import com.chenyumeng.marketplace.payout.domain.PayoutService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sellers")
public class SellerController {

    private final PayoutService payoutService;

    public SellerController(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    /** Total net amount owed to the seller (single currency, EUR, for now). */
    @GetMapping("/{sellerId}/balance")
    public BalanceResponse balance(@PathVariable String sellerId) {
        return new BalanceResponse(sellerId, payoutService.balanceOf(sellerId));
    }
}
