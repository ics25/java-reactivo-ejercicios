package com.fynalproject.demo.services;

import com.fynalproject.demo.domain.Balance;
import com.fynalproject.demo.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<User> createUser(User user);
    Mono<User> getUser(String id);
    Flux<User> getUsers();
    Mono<User> updateUserBalance(String id, Balance balance);
    Mono<Void> deleteUser(String id);
}
