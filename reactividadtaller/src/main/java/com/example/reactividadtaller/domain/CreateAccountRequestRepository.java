package com.example.reactividadtaller.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CreateAccountRequestRepository extends ReactiveCrudRepository<CreateAccountRequest, String> {

}
