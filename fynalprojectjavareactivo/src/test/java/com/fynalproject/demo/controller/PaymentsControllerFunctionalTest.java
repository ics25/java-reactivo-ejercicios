package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.Payment;
import com.fynalproject.demo.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PaymentsControllerFunctionalTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getPayments() {
        Payment payment = new Payment();
        payment.setUserId("3110");
        payment.setAmount(BigDecimal.valueOf(100.0));

        webTestClient.post()
                .uri("/payments")
                .bodyValue(payment)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentStatus.class)
                .isEqualTo(PaymentStatus.APPROVED);
    }

    @Test
    void getPayments_rejected() {
        Payment payment = new Payment();
        payment.setUserId("40");
        payment.setAmount(BigDecimal.valueOf(100.0));

        webTestClient.post()
                .uri("/payments")
                .bodyValue(payment)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentStatus.class)
                .isEqualTo(PaymentStatus.REJECTED);
    }

    @Test
    void getPayments_invalid() {
        Payment payment = new Payment();
        payment.setUserId("5");
        payment.setAmount(BigDecimal.valueOf(100.0));

        webTestClient.post()
                .uri("/payments")
                .bodyValue(payment)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentStatus.class)
                .isEqualTo(PaymentStatus.INVALID);
    }
}
