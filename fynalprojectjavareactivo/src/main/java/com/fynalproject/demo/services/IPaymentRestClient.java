package com.fynalproject.demo.services;

import com.fynalproject.demo.domain.Payment;
import com.fynalproject.demo.domain.PaymentStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IPaymentRestClient {
    @PostExchange("/payments")
    Mono<PaymentStatus> getPayments(@RequestBody Payment payment);
}
