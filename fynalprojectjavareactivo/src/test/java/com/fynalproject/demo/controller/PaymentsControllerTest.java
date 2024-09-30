package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.Payment;
import com.fynalproject.demo.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = {PaymentsController.class})
@WebFluxTest(PaymentsController.class)
public class PaymentsControllerTest {
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
                .expectBody(String.class)
                .value(response -> {
                    assertTrue(response.contains(PaymentStatus.APPROVED.name()));
                });
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
                .expectBody(String.class)
                .value(response -> {
                    assertTrue(response.contains(PaymentStatus.REJECTED.name()));
                });
    }

    @Test
    void getPayments_invalid() {
        Payment payment = new Payment();
        payment.setUserId("122225");
        payment.setAmount(BigDecimal.valueOf(100.0));

        webTestClient.post()
                .uri("/payments")
                .bodyValue(payment)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assertTrue(response.contains(PaymentStatus.INVALID.name()));
                });
    }
}
