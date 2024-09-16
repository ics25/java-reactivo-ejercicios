package com.bancking.demo.controlador;

import com.bancking.demo.domain.Transaction;
import com.bancking.demo.service.TransactionConfirmationService;
import com.bancking.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MainController {
    private final TransactionService transactionService;
    private final TransactionConfirmationService transactionConfirmationService;

    public MainController(TransactionService transactionService, TransactionConfirmationService transactionConfirmationService){

        this.transactionService = transactionService;
        this.transactionConfirmationService = transactionConfirmationService;
    }

    @PostMapping("/processTransactions")
    public Mono<Void> processTransaction(@RequestBody Flux<Transaction> transactions){
        Mono<Transaction> preparedTransactions = transactionService.prepareTransactions(transactions);
        return preparedTransactions
                .zipWhen(preparedTransaction -> transactionConfirmationService.confirmTransactions(Flux.just(preparedTransaction)))
                .then();

    }


}
