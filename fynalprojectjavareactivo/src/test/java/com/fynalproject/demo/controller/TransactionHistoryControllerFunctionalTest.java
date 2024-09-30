package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.CashOut;
import com.fynalproject.demo.domain.CashoutRepository;
import com.fynalproject.demo.domain.User;
import com.fynalproject.demo.domain.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionHistoryControllerFunctionalTest {

    private static final AtomicReference<String> userId = new AtomicReference<>();
    private static final AtomicReference<String> cashOutId = new AtomicReference<>();

    @Autowired
    private WebTestClient webTestClient;

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository, @Autowired CashoutRepository cashOutRepository) {
        cashOutRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @BeforeAll
    static void setup(@Autowired UserRepository userRepository, @Autowired CashoutRepository cashOutRepository) {
        User user = new User("Ivan", BigDecimal.valueOf(100.0));
        userRepository.save(user)
                .doOnSuccess(userDB -> userId.set(userDB.getId()))
                .block();

        CashOut cashOut = new CashOut();
        cashOut.setUserId(userId.get());
        cashOut.setAmount(BigDecimal.valueOf(10.0));
        cashOutRepository.save(cashOut)
                .doOnSuccess(cashOutDB -> cashOutId.set(cashOutDB.getId()))
                .block();
    }

    @Test
    @Order(1)
    void getTransactionHistoryByUserId() {
        webTestClient.get()
                .uri("/transaction-history/user/{userId}", userId.get())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CashOut.class);
    }

    @Test
    @Order(2)
    void getTransactionHistoryByUserId_notFound() {
        webTestClient.get()
                .uri("/transaction-history/user/{userId}", 0)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void getTransactionHistoryByCashOutId() {
        webTestClient.get()
                .uri("/transaction-history/cashout/{cashOutId}", cashOutId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashOut.class);
    }

    @Test
    @Order(4)
    void getTransactionHistoryByCashOutId_notFound() {
        webTestClient.get()
                .uri("/transaction-history/cashout/{cashOutId}", 0)
                .exchange()
                .expectStatus().isNotFound();
    }
}
