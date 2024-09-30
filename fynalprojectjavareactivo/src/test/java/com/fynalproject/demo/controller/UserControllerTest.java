package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.Balance;
import com.fynalproject.demo.domain.User;
import com.fynalproject.demo.exceptions.NotFoundUserException;
import com.fynalproject.demo.exceptions.responses.ErrorResponse;
import com.fynalproject.demo.exceptions.responses.ValidationErrorResponse;
import com.fynalproject.demo.services.IUserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {UserController.class, IUserService.class, GlobalHandlerError.class})
@WebFluxTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUser() {
        User user = new User();
        user.setId("1");
        user.setName("Ivancho");
        user.setBalance(BigDecimal.valueOf(3200.0));

        Mockito.when(userService.getUser("1")).thenReturn(Mono.just(user));

        webTestClient.get()
                .uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(userResponse -> {
                    assertEquals(user.getId(), userResponse.getId());
                });
    }

    @Test
    void getUser_NotFound() {
        String userId = "1";

        Mockito.when(userService.getUser(userId))
                .thenReturn(Mono.error(new NotFoundUserException("User not found")));

        webTestClient.get()
                .uri("/users/{id}", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals("User not found", errorResponse.getMessage());
                });
    }

    @Test
    void getUsers() {
        User userOne = new User();
        userOne.setId("1");
        userOne.setName("Ivancho");
        userOne.setBalance(BigDecimal.valueOf(300.0));

        User userTwo = new User();
        userTwo.setId("120");
        userTwo.setName("Jairo");
        userTwo.setBalance(BigDecimal.valueOf(400.0));

        Mockito.when(userService.getUsers()).thenReturn(Flux.just(userOne, userTwo));

        webTestClient.get()
                .uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .value(users -> {
                    assertEquals(2, users.size());
                });
    }

    @Test
    void createUser() {
        User userInput = new User();
        userInput.setName("Ivancho");
        userInput.setBalance(BigDecimal.valueOf(100.0));

        User userOutput = new User();
        userOutput.setId("1");
        userOutput.setName("Ivvancho");
        userOutput.setBalance(BigDecimal.valueOf(100.0));

        Mockito.when(userService.createUser(userInput)).thenReturn(Mono.just(userOutput));

        webTestClient.post()
                .uri("/users")
                .bodyValue(userInput)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
//          .value(userResponse -> {
//              assertEquals(userOutput.getId(), userResponse.getId());
//          })
        ;
    }

    @Test
    void createUser_badRequest() {
        User userInput = new User();
        ValidationErrorResponse.Violation violationBalance = new ValidationErrorResponse
                .Violation("balance", "Balance is required");
        ValidationErrorResponse.Violation violationName = new ValidationErrorResponse
                .Violation("name", "Name is required");

        List<ValidationErrorResponse.Violation> expectViolations = List.of(violationBalance, violationName);

        webTestClient.post()
                .uri("/users")
                .bodyValue(userInput)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(400, errorResponse.getCode());
                    assertEquals("BAD_REQUEST", errorResponse.getTitle());
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertEquals(expectViolations.size(), errorResponse.getErrors().size());
                    assertEquals(expectViolations.get(0).getCode(), errorResponse.getErrors().get(0).getCode());
                    assertEquals(expectViolations.get(0).getMessage(), errorResponse.getErrors().get(0).getMessage());
                    assertEquals(expectViolations.get(1).getCode(), errorResponse.getErrors().get(1).getCode());
                    assertEquals(expectViolations.get(1).getMessage(), errorResponse.getErrors().get(1).getMessage());
                });
    }

    @Test
    void updateUserBalance() {
        User user = new User();
        user.setId("1");
        user.setName("Ivancho");

        User userUpdate = new User();
        userUpdate.setId("1");
        userUpdate.setName("Ivancho");
        userUpdate.setBalance(BigDecimal.valueOf(200.0));

        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(100.0));

        Mockito.when(userService.getUser("1")).thenReturn(Mono.just(user));
        Mockito.when(userService.updateUserBalance(user.getId(), balance)).thenReturn(Mono.just(userUpdate));

        webTestClient.put()
                .uri("/users/{id}", user.getId())
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
//          .value(userResponse -> {
//              assertEquals(userUpdate.getBalance(), userResponse.getBalance());
//          })
        ;
    }

    @Test
    void updateUserBalance_badRequest() {

        Balance balance = new Balance();
        //balance.setAmount(BigDecimal.valueOf(0.0));


        ValidationErrorResponse.Violation violationAmount = new ValidationErrorResponse
                .Violation("amount", "Amount is required");

        List<ValidationErrorResponse.Violation> expectViolations = List.of(violationAmount);

        webTestClient.put()
                .uri("/users/{id}", 2)
                .bodyValue(balance)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(400, errorResponse.getCode());
                    assertEquals("BAD_REQUEST", errorResponse.getTitle());
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertEquals(expectViolations.size(), errorResponse.getErrors().size());
                    assertEquals(expectViolations.get(0).getCode(), errorResponse.getErrors().get(0).getCode());
                    assertEquals(expectViolations.get(0).getMessage(), errorResponse.getErrors().get(0).getMessage());
                });
    }

    @Test
    void deleteUser() {
        String userId = "1";

        Mockito.when(userService.deleteUser(userId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/users/{id}", userId)
                .exchange()
                .expectStatus().isOk();
    }
}
