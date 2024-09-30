package com.fynalproject.demo.services;

import com.fynalproject.demo.domain.CashOut;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICashOutService {
    Mono<CashOut> createCashOut(CashOut cashOut);
    Mono<CashOut> getCashOut(String id);
    Flux<CashOut> getCashOutsByUserId(String userId);
}
