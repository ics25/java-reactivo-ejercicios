package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.CashOut;
import com.fynalproject.demo.services.ICashOutService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transaction-history")
public class TransactionHistoryController {

    private final ICashOutService cashOutService;

    public TransactionHistoryController(ICashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @GetMapping("/user/{userId}")
    public Flux<CashOut> getTransactionHistoryByUserId(@PathVariable("userId") String userId) {
        return cashOutService.getCashOutsByUserId(userId);
    }

    @GetMapping("/cashout/{cashOutId}")
    public Mono<CashOut> getTransactionHistoryByCashOutId(@PathVariable("cashOutId") String cashOutId) {
        return cashOutService.getCashOut(cashOutId);
    }
}
