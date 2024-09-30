package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.CashOut;
import com.fynalproject.demo.services.ICashOutService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/cashouts")
public class CashOutController {

    private final ICashOutService cashOutService;

    public CashOutController(ICashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @RequestMapping("/user/{userId}")
    public Flux<CashOut> getCashOutsByUserId(@PathVariable("userId") String userId) {
        return cashOutService.getCashOutsByUserId(userId);
    }

    @PostMapping
    public Mono<CashOut> createCashOut(@Valid @RequestBody CashOut cashOut) {
        return cashOutService.createCashOut(cashOut);
    }
}
