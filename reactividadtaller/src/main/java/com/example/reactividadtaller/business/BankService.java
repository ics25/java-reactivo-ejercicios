package com.example.reactividadtaller.business;

import com.example.reactividadtaller.BusinessException;
import com.example.reactividadtaller.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class BankService {

    private final CreateAccountRequestRepository createAccountRequestRepository;
    private final TransferRequestRepository transferRequestRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    public BankService(CreateAccountRequestRepository createAccountRequestRepository,
                       TransferRequestRepository transferRequestRepository,
                       TransactionRepository transactionRepository,
                       LoanRepository loanRepository,
                       CustomerProfileRepository customerProfileRepository) {
        this.createAccountRequestRepository = createAccountRequestRepository;
        this.transferRequestRepository = transferRequestRepository;
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    public Mono<Double> getBalance(String accountId) {
        // Caso de uso: Consultar el saldo actual de una cuenta bancaria. Sino hay balance se debe tener un valor de 0.0

        return createAccountRequestRepository.findById(accountId)
                .map(CreateAccountRequest::getInitialBalance)
                .switchIfEmpty(Mono.just(0.0));
        // Implementar la lógica de consulta aquí
    }

    public Mono<String> transferMoney(TransferRequest request) {
        // Caso de uso: Transferir dinero de una cuenta a otra. Hacer llamado de otro flujo simulando el llamado
        return createAccountRequestRepository.findById(request.getFromAccount())
                .flatMap(sourceAccount -> {
                    if (sourceAccount.getInitialBalance() < request.getAmount()) {
                        return Mono.error(new IllegalArgumentException("Insufficient funds"));
                    }
                    sourceAccount.setInitialBalance(sourceAccount.getInitialBalance() - request.getAmount());
                    return createAccountRequestRepository.save(sourceAccount);
                })
                .then(createAccountRequestRepository.findById(request.getToAccount())
                        .flatMap(destinationAccount -> {
                            destinationAccount.setInitialBalance(destinationAccount.getInitialBalance() + request.getAmount());
                            return createAccountRequestRepository.save(destinationAccount);
                        }))
                .then(transactionRepository.save(new Transaction(null, request.getFromAccount(), -request.getAmount())))
                .then(transactionRepository.save(new Transaction(null, request.getToAccount(), request.getAmount())))
                .then(Mono.just("Transfer successful"))
                .onErrorResume(e -> Mono.just("Transfer failed: " + e.getMessage()));
        // Implementar la lógica de consulta aquí  ???????
    }

    public Flux<Transaction> getTransactions(String accountId) {
        // Caso de uso: Consultar el historial de transacciones de una cuenta bancaria.
        List<Transaction> transactions = Arrays.asList(
                new Transaction("1", accountId, 200.00),
                new Transaction("2", accountId, -150.00),
                new Transaction("3", accountId, 300.00)
        );
        return transactionRepository.findByAccountId(accountId)
                .switchIfEmpty(Flux.empty()); // Implementar la lógica de consulta aquí
    }

    public Mono<String> createAccount(CreateAccountRequest request) {
        // Caso de uso: Crear una nueva cuenta bancaria con un saldo inicial.
        return createAccountRequestRepository.save(request)
                .then(Mono.just("Account created successfully with initial balance: " + request.getInitialBalance()))
                .onErrorResume(e -> Mono.just("Failed to create account: " + e.getMessage()));// Implementar la lógica de consulta aquí
    }

    public Mono<String> createCustomerProfile(CustomerProfile customer) {
        // Caso de uso: Crear una nueva cuenta bancaria con un saldo inicial.
        return customerProfileRepository.save(customer)
                .then(Mono.just("Customer created successfully "))
                .onErrorResume(e -> Mono.just("Failed to create account: " + e.getMessage()));// Implementar la lógica de consulta aquí
    }

    public Mono<String> closeAccount(String accountId) {
        // Caso de uso: Cerrar una cuenta bancaria especificada. Verificar que la ceunta exista y si no existe debe retornar un error controlado
        return createAccountRequestRepository.findById(accountId)
                .flatMap(account -> createAccountRequestRepository.delete(account)
                        .then(Mono.just("Account closed successfully")))
                .switchIfEmpty(Mono.error(new BusinessException("Account not found: " + accountId)));// Implementar la lógica de consulta aquí
    }

    public Mono<String> updateAccount(UpdateAccountRequest request) {
        // Caso de uso: Actualizar la información de una cuenta bancaria especificada. Verificar que la ceunta exista y si no existe debe retornar un error controlado
        return Mono.empty(); // Implementar la lógica de consulta aquí
    }

    public Mono<CustomerProfile> getCustomerProfile(String accountId) {
        // Caso de uso: Consultar el perfil del cliente que posee la cuenta bancaria. Obtener los valores por cada uno de los flujos y si no existe alguno debe presentar un error
        return customerProfileRepository.findByAccountId(accountId)
                .switchIfEmpty(Mono.empty());  // Implementar la lógica de consulta aquí
    }

    public Flux<Loan> getActiveLoans(String customerId) {
        // Caso de uso: Consultar todos los préstamos activos asociados al cliente especificado.
        return loanRepository.findByCustomerIdAndActive(customerId, true)
                .switchIfEmpty(Flux.error(new BusinessException("No active loans found for customer: " + customerId)));// Implementar la lógica de consulta aquí
    }

    public Flux<Double> simulateInterest(String accountId) {
        // Caso de uso: Simular el interés compuesto en una cuenta bancaria. Sacar un rago de 10 años y aplicar la siguiente formula = principal * Math.pow(1 + rate, year)
        return customerProfileRepository.findByAccountId(accountId)
                .flatMapMany(customerProfile -> loanRepository.findByCustomerId(customerProfile.getCustomerId())
                        .next()
                        .flatMapMany(loan -> {
                            double principal = loan.getBalance();
                            double rate = loan.getInterestRate();

                            return Flux.range(1, 10)
                                    .map(year -> principal * Math.pow(1 + rate, year));
                        })
                )
                .switchIfEmpty(Flux.error(new BusinessException("No active loan found for account: " + accountId))); // Implementar la lógica de consulta aquí
    }

    public Mono<String> getLoanStatus(String loanId) {
        // Caso de uso: Consultar el estado de un préstamo. se debe tener un flujo balanceMono y interestRateMono. Imprimir con el formato siguiente el resultado   "Loan ID: %s, Balance: %.2f, Interest Rate: %.2f%%"
        return Mono.empty(); // Implementar la lógica de consulta aquí
    }


}