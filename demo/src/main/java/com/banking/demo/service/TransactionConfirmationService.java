package com.banking.demo.service;
import com.banking.demo.domain.Transaction;
import com.banking.demo.domain.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TransactionConfirmationService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private TransactionRepository transactionRepository;

    public Mono<Void> confirmTransactions(Flux<Transaction> transactions) {
        return transactions
                .buffer(10)
                .flatMap(this::executeTransactions)
                .then();
    }

    private Mono<Void> executeTransactions(List<Transaction> transactions) {
        WebClient webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
        return webClient.post()
                .uri("/executeBatch")
                .bodyValue(transactions)
                .retrieve()
                .bodyToMono(Void.class)
                .then(transactionRepository.saveAll(transactions).then()); // Guardar confirmaciones en la base de datos
    }
}