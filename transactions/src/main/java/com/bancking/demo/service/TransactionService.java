package com.bancking.demo.service;

import com.bancking.demo.domain.Transaction;
import com.bancking.demo.domain.TransactionRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private TransactionRepository transactionRepository;

    public Mono<Transaction> prepareTransactions(Flux<Transaction> transactions){
        return transactions
                .map(this::validateTransaction)
                .flatMap(this::calculateFees)
                .reduce(this::aggregateTransactions)
                .flatMap(transactionRepository::save);
    }

    private Transaction validateTransaction(Transaction transaction) {
        if(transaction.getAmount() <= 0){
            throw new IllegalArgumentException("Invalid transaction amount");
        }
        return transaction;
    }

    private  Mono<Transaction> calculateFees(Transaction transaction) {
        WebClient client = webClientBuilder.baseUrl("http://localhost:8090").build();

        return client.post()
                .uri("/calculateFees")
                .bodyValue(transaction)
                .retrieve()
                .bodyToMono(Transaction.class)
                .map(res -> {
                    transaction.setFee(res.getFee());
                    return transaction;
                });
    }

    private Transaction aggregateTransactions(Transaction t1, Transaction t2) {
        t1.setAmount(t1.getAmount() + t2.getAmount());
        t1.setFee(t1.getFee() + t2.getFee());
        return t1;
    }


}
