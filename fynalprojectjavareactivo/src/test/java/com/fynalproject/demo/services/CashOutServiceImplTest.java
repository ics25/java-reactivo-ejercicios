package com.fynalproject.demo.services;

import com.fynalproject.demo.domain.*;
import com.fynalproject.demo.exceptions.NotFoundUserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class CashOutServiceImplTest {

    @Mock
    private IUserService userService;

    @Mock
    private CashoutRepository cashOutRepository;

    @Mock
    private IPaymentRestClient paymentRestClient;

    @InjectMocks
    private CashOutServiceImpl cashOutService;

    @Test
    void createCashOut() {
        User user = new User();
        user.setId("1");
        user.setBalance(BigDecimal.valueOf(200.0));
        user.setName("Jorge");

        CashOut cashOut = new CashOut();
        cashOut.setUserId(user.getId());
        cashOut.setAmount(BigDecimal.valueOf(-50.0));

        Mockito.when(userService.getUser("1")).thenReturn(Mono.just(user));
        Mockito.when(userService.updateUserBalance(Mockito.eq(user.getId()), Mockito.any(Balance.class))).thenReturn(Mono.just(user));
        Mockito.when(paymentRestClient.getPayments(Mockito.any(Payment.class))).thenReturn(Mono.just(PaymentStatus.APPROVED));
        Mockito.when(cashOutRepository.save(cashOut)).thenReturn(Mono.just(cashOut));

        StepVerifier.create(cashOutService.createCashOut(cashOut))
                .expectNext(cashOut)
                .verifyComplete();
    }

    @Test
    void createCashOut_insufficientBalance() {
        User user = new User();
        user.setId("1");
        user.setBalance(BigDecimal.valueOf(100.0));
        user.setName("Ivancho");

        CashOut cashOut = new CashOut();
        cashOut.setUserId(user.getId());
        cashOut.setAmount(BigDecimal.valueOf(-500.0));

        Mockito.when(userService.getUser("1")).thenReturn(Mono.just(user));

        StepVerifier.create(cashOutService.createCashOut(cashOut))
                .expectError()
                .verify();
    }

    @Test
    void createCashOut_PaymentNoApproved() {
        User user = new User();
        user.setId("1");
        user.setBalance(BigDecimal.valueOf(100.0));
        user.setName("Jorge");

        CashOut cashOut = new CashOut();
        cashOut.setUserId(user.getId());
        cashOut.setAmount(BigDecimal.valueOf(-50.0));

        Mockito.when(userService.getUser("1")).thenReturn(Mono.just(user));
        Mockito.when(paymentRestClient.getPayments(Mockito.any(Payment.class)))
                .thenReturn(Mono.just(PaymentStatus.REJECTED));

        StepVerifier.create(cashOutService.createCashOut(cashOut))
                .expectError()
                .verify();
    }

    @Test
    void createCashOut_notFound(){
        CashOut cashOut = new CashOut();
        cashOut.setUserId("1");
        cashOut.setAmount(BigDecimal.valueOf(100.0));

        Mockito.when(userService.getUser("1"))
                .thenReturn(Mono.error(new NotFoundUserException("User not found")));

        StepVerifier.create(cashOutService.createCashOut(cashOut))
                .expectError()
                .verify();
    }

    @Test
    void getCashOut() {
        CashOut cashOut = new CashOut();
        cashOut.setId("1");
        cashOut.setUserId("1");
        cashOut.setAmount(BigDecimal.valueOf(200.0));

        Mockito.when(cashOutRepository.findById("1")).thenReturn(Mono.just(cashOut));

        StepVerifier.create(cashOutService.getCashOut("1"))
                .expectNext(cashOut)
                .verifyComplete();
    }

    @Test
    void getCashOut_notFound() {
        Mockito.when(cashOutRepository.findById("1")).thenReturn(Mono.empty());

        StepVerifier.create(cashOutService.getCashOut("1"))
                .expectError()
                .verify();
    }

    @Test
    void getCashOutsByUserId() {
        CashOut cashOutOne = new CashOut();
        cashOutOne.setId("1");
        cashOutOne.setUserId("1");
        cashOutOne.setAmount(BigDecimal.valueOf(50.0));

        CashOut cashOutTwo = new CashOut();
        cashOutTwo.setId("1");
        cashOutTwo.setUserId("1");
        cashOutTwo.setAmount(BigDecimal.valueOf(-100.0));


        Mockito.when(cashOutRepository.findByUserId("1")).thenReturn(Flux.just(cashOutOne, cashOutTwo));

        StepVerifier.create(cashOutService.getCashOutsByUserId("1"))
                .expectNext(cashOutOne)
                .expectNext(cashOutTwo)
                .verifyComplete();
    }

    @Test
    void getCashOutsByUserId_notFound() {
        Mockito.when(cashOutRepository.findByUserId("1")).thenReturn(Flux.empty());

        StepVerifier.create(cashOutService.getCashOutsByUserId("1"))
                .expectError()
                .verify();
    }
}
