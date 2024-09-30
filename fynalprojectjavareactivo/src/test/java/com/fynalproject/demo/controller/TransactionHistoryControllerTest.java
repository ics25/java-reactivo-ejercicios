package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.CashOut;
import com.fynalproject.demo.exceptions.NotFoundCashOutException;
import com.fynalproject.demo.exceptions.responses.ErrorResponse;
import com.fynalproject.demo.services.CashOutServiceImpl;
import com.fynalproject.demo.services.ICashOutService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {TransactionHistoryController.class, ICashOutService.class, GlobalHandlerError.class})
@WebFluxTest(TransactionHistoryController.class)
public class TransactionHistoryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CashOutServiceImpl cashOutService;

    @InjectMocks
    private TransactionHistoryController transactionHistoryController;

    @Test
    void getTransactionHistoryByUserId() {
        CashOut cashOutOne = new CashOut();
        cashOutOne.setId("1");
        cashOutOne.setUserId("1");
        cashOutOne.setAmount(BigDecimal.valueOf(100.0));

        CashOut cashOutTwo = new CashOut();
        cashOutTwo.setId("2");
        cashOutTwo.setUserId("1");
        cashOutTwo.setAmount(BigDecimal.valueOf(200.0));

        Mockito.when(cashOutService.getCashOutsByUserId("1")).thenReturn(Flux.just(cashOutOne, cashOutTwo));

        webTestClient.get()
                .uri("/transaction-history/user/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CashOut.class)
                .value(cashOuts -> {
                    assertEquals(2, cashOuts.size());
                    assertEquals(cashOutOne.getId(), cashOuts.get(0).getId());
                    assertEquals(cashOutOne.getAmount(), cashOuts.get(0).getAmount());
                    assertEquals(cashOutOne.getUserId(), cashOuts.get(0).getUserId());
                    assertEquals(cashOutTwo.getId(), cashOuts.get(1).getId());
                    assertEquals(cashOutTwo.getAmount(), cashOuts.get(1).getAmount());
                    assertEquals(cashOutTwo.getUserId(), cashOuts.get(1).getUserId());
                });
    }

    @Test
    void getTransactionHistoryByUserId_notFound() {
        String userId = "1";

        Mockito.when(cashOutService.getCashOutsByUserId(userId))
                .thenReturn(Flux.error(new NotFoundCashOutException("CashOut not found")));

        webTestClient.get()
                .uri("/transaction-history/user/{userId}", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(404, errorResponse.getCode());
                    assertEquals("NOT_FOUND", errorResponse.getTitle());
                    assertEquals("CashOut not found", errorResponse.getMessage());
                });
    }

    @Test
    void getTransactionHistoryByCashOutId() {
        CashOut cashOut = new CashOut();
        cashOut.setId("1");
        cashOut.setUserId("1");
        cashOut.setAmount(BigDecimal.valueOf(200.0));

        Mockito.when(cashOutService.getCashOut(cashOut.getId())).thenReturn(Mono.just(cashOut));

        webTestClient.get()
                .uri("/transaction-history/cashout/{cashOutId}", cashOut.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CashOut.class)
                .value(cashOutResponse -> {
                    assertEquals(cashOut.getId(), cashOutResponse.getId());
                    assertEquals(cashOut.getAmount(), cashOutResponse.getAmount());
                    assertEquals(cashOut.getUserId(), cashOutResponse.getUserId());
                });
    }

    @Test
    void getTransactionHistoryByCashOutId_notFound() {
        String cashOutId = "1";

        Mockito.when(cashOutService.getCashOut(cashOutId))
                .thenReturn(Mono.error(new NotFoundCashOutException("CashOut not found")));

        webTestClient.get()
                .uri("/transaction-history/cashout/{cashOutId}", cashOutId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(404, errorResponse.getCode());
                    assertEquals("NOT_FOUND", errorResponse.getTitle());
                    assertEquals("CashOut not found", errorResponse.getMessage());
                });
    }

}
