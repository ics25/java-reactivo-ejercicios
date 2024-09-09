package com.example.reactividadtaller.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface CustomerProfileRepository extends ReactiveCrudRepository<CustomerProfile, String> {

    Mono<CustomerProfile> findByAccountId(String accountId);

}
