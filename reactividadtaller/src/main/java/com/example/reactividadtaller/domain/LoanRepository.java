package com.example.reactividadtaller.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface LoanRepository extends ReactiveCrudRepository<Loan, String> {
    Flux<Loan> findByCustomerIdAndActive(String customerId, boolean active);
    Flux<Loan> findByCustomerId(String customerId);
}
