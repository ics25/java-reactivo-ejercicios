package com.bancking.demo.service;

import com.bancking.demo.domain.Transaction;
import com.bancking.demo.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec uriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec headerSpec;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void prepareTransactions() {
        WebClient.RequestBodyUriSpec bodySpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec response = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headerSpec);
        doReturn(bodySpec).when(headerSpec).bodyValue(any(Transaction.class));
        when(bodySpec.retrieve()).thenReturn(response);
        when(response.bodyToMono(Transaction.class)).thenReturn(Mono.just(new Transaction()));

        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.empty());

        Transaction t1 = new Transaction();
        t1.setAmount(100.0);
        Transaction t2 = new Transaction();
        t2.setAmount(-50.0);

        List<Transaction> transactionList = Arrays.asList(t1, t2);
        Flux<Transaction> transactionFlux = Flux.fromIterable(transactionList);

        // Verificar que se lanza una excepción para transacciones con monto negativo
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.prepareTransactions(transactionFlux).block();
        });

        // Verificar que las transacciones se guardan correctamente
        Transaction t3 = new Transaction();
        t3.setAmount(200.0);
        List<Transaction> validTransactionList = Arrays.asList(t1, t3);
        Flux<Transaction> validTransactionFlux = Flux.fromIterable(validTransactionList);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(t1));

        Mono<Transaction> result = transactionService.prepareTransactions(validTransactionFlux);

        StepVerifier.create(result)
                .verifyComplete();

        verify(webClient, times(2)).post();  // Ajustar el número de invocaciones esperadas
        verify(transactionRepository, times(2)).save(any(Transaction.class));
}
}