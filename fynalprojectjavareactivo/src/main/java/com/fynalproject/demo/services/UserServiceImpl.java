package com.fynalproject.demo.services;

import com.fynalproject.demo.domain.Balance;
import com.fynalproject.demo.domain.User;
import com.fynalproject.demo.domain.UserRepository;
import com.fynalproject.demo.exceptions.NotFoundUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    public Mono<User> getUser(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundUserException("User not found")));
    }

    public Flux<User> getUsers() {
        return userRepository.findAll();
    }

    public Mono<User> updateUserBalance(String id, Balance balance) {
        return getUser(id)
                .flatMap(existingUser -> {
                    existingUser.setBalance(existingUser.getBalance().add(balance.getAmount()));
                    return userRepository.save(existingUser);
                });
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }
}
