package com.fynalproject.demo.services;

import com.fynalproject.demo.domain.Balance;
import com.fynalproject.demo.domain.User;
import com.fynalproject.demo.domain.UserRepository;
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
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServices;

    @Test
    void createUser() {
        User userInput = new User();
        userInput.setName("Ivancho");
        userInput.setBalance(BigDecimal.valueOf(50.0));

        User userOutput = new User();
        userOutput.setId("1");
        userOutput.setName("Ivancho");
        userOutput.setBalance(BigDecimal.valueOf(80.0));

        Mockito.when(userRepository.save(userInput)).thenReturn(Mono.just(userOutput));

        StepVerifier.create(userServices.createUser(userInput))
                .expectNext(userOutput)
                .verifyComplete();
    }

    @Test
    void getUser() {
        User userOutput = new User();
        userOutput.setId("1");
        userOutput.setName("Ivancho");
        userOutput.setBalance(BigDecimal.valueOf(200.0));

        Mockito.when(userRepository.findById("1")).thenReturn(Mono.just(userOutput));

        StepVerifier.create(userServices.getUser("1"))
                .expectNext(userOutput)
                .verifyComplete();
    }

    @Test
    void getUser_notFound() {
        Mockito.when(userRepository.findById("1")).thenReturn(Mono.empty());

        StepVerifier.create(userServices.getUser("1"))
                .verifyError();
    }

    @Test
    void getUsers() {
        User userOutputOne = new User();
        userOutputOne.setId("1");
        userOutputOne.setName("Ivancho");
        userOutputOne.setBalance(BigDecimal.valueOf(200.0));

        User userOutputTwo = new User();
        userOutputTwo.setId("2");
        userOutputTwo.setName("Isaac");
        userOutputTwo.setBalance(BigDecimal.valueOf(150.0));

        Mockito.when(userRepository.findAll()).thenReturn(Flux.just(userOutputOne, userOutputTwo));

        StepVerifier.create(userServices.getUsers())
                .expectNext(userOutputOne)
                .expectNext(userOutputTwo)
                .verifyComplete();
    }

    @Test
    void updateUserBalance() {
        User userOutput = new User();
        userOutput.setId("2");
        userOutput.setName("Ivancho");
        userOutput.setBalance(BigDecimal.valueOf(200.0));

        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(100.0));

        User userUpdated = new User();
        userUpdated.setId("1");
        userUpdated.setName("Ivancho");
        userUpdated.setBalance(BigDecimal.valueOf(200.0));

        Mockito.when(userRepository.findById("1")).thenReturn(Mono.just(userOutput));
        Mockito.when(userRepository.save(userOutput)).thenReturn(Mono.just(userUpdated));

        StepVerifier.create(userServices.updateUserBalance("1",balance))
                .expectNext(userUpdated)
                .verifyComplete();
    }

    @Test
    void deleteUser() {
        Mockito.when(userRepository.deleteById("1")).thenReturn(Mono.empty());

        StepVerifier.create(userServices.deleteUser("1"))
                .verifyComplete();
    }
}
