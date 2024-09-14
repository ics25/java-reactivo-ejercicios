package com.banking.demo.controller;

import com.banking.demo.domain.Transaction;
import com.banking.demo.service.TransactionConfirmationService;
import com.banking.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MainController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionConfirmationService transactionConfirmationService;

    @PostMapping("/processTransactions")
    public Mono<Void> processTransactions(@RequestBody Flux<Transaction> transactions) {
        Mono<Transaction> preparedTransactions = transactionService.prepareTransactions(transactions);

        return preparedTransactions
                .zipWhen(preparedTransaction -> transactionConfirmationService.confirmTransactions(Flux.just(preparedTransaction)))
                .then();
    }
}