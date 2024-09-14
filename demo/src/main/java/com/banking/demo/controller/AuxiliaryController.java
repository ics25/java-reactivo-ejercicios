package com.banking.demo.controller;

import com.banking.demo.domain.Transaction;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class AuxiliaryController {

    @PostMapping("/calculateFees")
    public Mono<Transaction> calculateFeesEndpoint(@RequestBody Transaction transaction) {
        // Simular cálculo de comisiones
        transaction.setFee(transaction.getAmount() * 0.02); // Ejemplo de una comisión del 2%
        return Mono.just(transaction);
    }

    @PostMapping("/executeBatch")
    public Mono<Void> executeBatchEndpoint(@RequestBody List<Transaction> transactions) {
        // Simular ejecución de transacciones
        transactions.forEach(transaction -> {
            // Lógica de ejecución
            System.out.println("Executing transaction: " + transaction);
        });
        return Mono.empty();
    }
}