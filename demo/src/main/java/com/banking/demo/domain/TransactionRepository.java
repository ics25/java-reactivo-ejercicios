package com.banking.demo.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

}
