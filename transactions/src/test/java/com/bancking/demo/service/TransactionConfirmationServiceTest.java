package com.bancking.demo.service;

import com.bancking.demo.domain.Transaction;
import com.bancking.demo.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionConfirmationServiceTest {

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
    private TransactionConfirmationService transactionConfirmationService;


    @Test
    void confirmTransactions() {

        WebClient.RequestBodyUriSpec bodySpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.ResponseSpec response = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headerSpec);
        doReturn(bodySpec).when(headerSpec).bodyValue(any(List.class));
        when(bodySpec.retrieve()).thenReturn(response);
        when(response.bodyToMono(Void.class)).thenReturn(Mono.empty());


        when(transactionRepository.saveAll(anyList())).thenReturn(Flux.empty());

        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();

        List<Transaction> transactionList = Arrays.asList(t1, t2);
        Flux<Transaction> transactionFlux = Flux.fromIterable(transactionList);

        Mono<Void> result = transactionConfirmationService.confirmTransactions(transactionFlux);

        StepVerifier.create(result)
                .verifyComplete();

        verify(webClient).post();
        verify(transactionRepository).saveAll(transactionList);


    }

}