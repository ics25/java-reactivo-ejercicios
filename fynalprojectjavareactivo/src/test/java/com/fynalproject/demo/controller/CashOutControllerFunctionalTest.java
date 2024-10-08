package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.CashOut;
import com.fynalproject.demo.domain.CashoutRepository;
import com.fynalproject.demo.domain.User;
import com.fynalproject.demo.domain.UserRepository;
import com.fynalproject.demo.exceptions.responses.ErrorResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CashOutControllerFunctionalTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final AtomicReference<String> userId = new AtomicReference<>();
    private static final AtomicReference<String> userIdInsufficientBalance =  new AtomicReference<>();
    private static final AtomicReference<String> userIdPaymentNoApproved = new AtomicReference<>();

    @BeforeAll
    static void setup(@Autowired UserRepository userRepository, @Autowired CashoutRepository cashOutRepository) {
        cashOutRepository.deleteAll().block();
        userRepository.deleteAll().block();

        User user = new User();
        user.setName("Ivan");
        user.setBalance(BigDecimal.valueOf(10000.0));

        userRepository.save(user)
                .doOnSuccess(userDB -> userId.set(userDB.getId()))
                .block();

        User userInsufficientBalance = new User();
        userInsufficientBalance.setName("Mauricio");
        userInsufficientBalance.setBalance(BigDecimal.valueOf(100.0));

        userRepository.save(userInsufficientBalance)
                .doOnSuccess(userDB -> userIdInsufficientBalance.set(userDB.getId()))
                .block();

        User userPaymentNoApproved = new User();
        userPaymentNoApproved.setName("Karol");
        userPaymentNoApproved.setBalance(BigDecimal.valueOf(1000.0));

        userRepository.save(userPaymentNoApproved)
                .doOnSuccess(userDB -> userIdPaymentNoApproved.set(userDB.getId()))
                .block();

    }

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository, @Autowired CashoutRepository cashOutRepository) {
        cashOutRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    @Order(1)
    void createCashOut() {
        CashOut cashOut = new CashOut();
        cashOut.setUserId(userId.get());
        cashOut.setAmount(BigDecimal.valueOf(100.0));

        webTestClient.post()
                .uri("/cashouts")
                .bodyValue(cashOut)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashOut.class)
                .value(cashOutResponse -> {
                    assertEquals(cashOut.getUserId(), cashOutResponse.getUserId());
                    assertEquals(cashOut.getAmount(), cashOutResponse.getAmount());
                });
    }

    @Test
    @Order(2)
    void createCashOut_badRequest() {
        CashOut cashOut = new CashOut();
        cashOut.setAmount(BigDecimal.valueOf(100.0));

        webTestClient.post()
                .uri("/cashouts")
                .bodyValue(cashOut)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
        ;
    }

    @Test
    void createCashOut_insufficientBalance(){
        CashOut cashOut = new CashOut();
        cashOut.setUserId(userIdInsufficientBalance.get());
        cashOut.setAmount(BigDecimal.valueOf(10000.0));

        webTestClient.post()
                .uri("/cashouts")
                .bodyValue(cashOut)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals("Insufficient balance", errorResponse.getMessage());
                });
    }

    @Test
    void createCashOut_PaymentNoApproved() {
        CashOut cashOut = new CashOut();
        cashOut.setUserId(userIdPaymentNoApproved.get());
        cashOut.setAmount(BigDecimal.valueOf(100.0));

        webTestClient.post()
                .uri("/cashouts")
                .bodyValue(cashOut)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals("Payment not approved", errorResponse.getMessage());
                });
    }

    @Test
    void createCashOut_notFound(){
        CashOut cashOut = new CashOut();
        cashOut.setUserId("0");
        cashOut.setAmount(BigDecimal.valueOf(100.0));

        webTestClient.post()
                .uri("/cashouts")
                .bodyValue(cashOut)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals("User not found", errorResponse.getMessage());
                });
    }

    @Test
    @Order(3)
    void getCashOutsByUserId() {
        webTestClient.get()
                .uri("/cashouts/user/{userId}", userId.get())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CashOut.class);
    }

    @Test
    @Order(4)
    void getCashOutsByUserId_notFound() {
        webTestClient.get()
                .uri("/cashouts/user/{userId}", 0)
                .exchange()
                .expectStatus().isNotFound();
    }
}
