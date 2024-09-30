package com.fynalproject.demo.services;

import com.fynalproject.demo.domain.*;
import com.fynalproject.demo.exceptions.NotFoundCashOutException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
@Service
public class CashOutServiceImpl implements ICashOutService{
    private final IUserService userService;

    private final CashoutRepository cashOutRepository;

    private final IPaymentRestClient paymentRestClient;

    public CashOutServiceImpl(
            IUserService userService,
            CashoutRepository cashOutRepository,
            IPaymentRestClient paymentRestClient) {
        this.userService = userService;
        this.cashOutRepository = cashOutRepository;
        this.paymentRestClient = paymentRestClient;
    }

    public Mono<CashOut> createCashOut(CashOut cashOut) {
        return userService.getUser(cashOut.getUserId())
                .flatMap(verifyBalance(cashOut))
                .flatMap(createPayment(cashOut))
                .flatMap(saveCashOut(cashOut))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Error creating cashOut")));
    }

    private Function<User, Mono<User>> verifyBalance(CashOut cashOut) {
        return user -> {
            int compareBalanceWithAmount = user.getBalance().compareTo(cashOut.getAmount());
            if (compareBalanceWithAmount < 0) {
                return Mono.error(new IllegalArgumentException("Insufficient balance"));
            }
            return Mono.just(user);
        };
    }

    private Function<User, Mono<User>> createPayment(CashOut cashOut) {
        return user -> {
            Payment payment = new Payment(cashOut.getUserId(), cashOut.getAmount());
            return paymentRestClient.getPayments(payment)
                    .flatMap(paymentStatus -> {
                        if (!paymentStatus.equals(PaymentStatus.APPROVED)) {
                            return Mono.error(new IllegalArgumentException("Payment not approved"));
                        }
                        Balance balance = new Balance(cashOut.getAmount());
                        return userService.updateUserBalance(user.getId(), balance);
                    });
        };
    }

    private Function<User, Mono<? extends CashOut>> saveCashOut(CashOut cashOut) {
        return user -> cashOutRepository.save(cashOut);
    }

    public Mono<CashOut> getCashOut(String id) {
        return cashOutRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundCashOutException("CashOut not found")));
    }

    public Flux<CashOut> getCashOutsByUserId(String userId) {
        return cashOutRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundCashOutException("CashOut not found")));
    }
}
