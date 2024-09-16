package com.bancking.demo.controlador;

import com.bancking.demo.domain.Transaction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class AuxiliaryController {

    @PostMapping("/calculateFees")
    public Mono<Transaction> calculateFees(@RequestBody Transaction transaction){
        //simular el calculo
        transaction.setFee(transaction.getAmount() * 0.02);// comision del 2%
        return Mono.just(transaction);
    }

    @PostMapping("/executeBatch")
    public Mono<Void> executeBatch(@RequestBody List<Transaction> transactions){
        //simular procesamiento de batch

        transactions.forEach(transaction -> {
            System.out.println("Ejecutar transacion "+transaction);
        });

        return Mono.empty();
    }
}
