package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);


		Flux<Transaction> transactions = Flux.just(new Transaction(100), new Transaction(200));
		Flux<Integer> rewards = transactions.map(tx -> tx.getAmount() * 2);
		rewards.subscribe(System.out::println); // Outputs: 200, 400


		Flux<Transaction> transactions1 = Flux.just(new Transaction(50), new Transaction(150));
		Flux<Transaction> largeTransactions = transactions1.filter(tx -> tx.getAmount() > 100);
		largeTransactions.subscribe(tx -> System.out.println(tx.getAmount())); // Outputs: 150

		Flux<Transaction> transactions2 = Flux.just(new Transaction(100), new Transaction(200));
		Flux<User> users = Flux.just(new User("Alice"), new User("Bob"));
		Flux<String> transactionUserDetails = Flux.zip(transactions, users, (tx, user) -> user.getName() + " made a transaction of " + tx.getAmount());
		transactionUserDetails.subscribe(System.out::println); // Outputs: Alice made a transaction of 100, Bob made a transaction of 200

		Flux<Transaction> account1Transactions = Flux.just(new Transaction(100), new Transaction(200));
		Flux<Transaction> account2Transactions = Flux.just(new Transaction(300), new Transaction(400));
		Flux<Transaction> allTransactions = Flux.merge(account1Transactions, account2Transactions);
		allTransactions.subscribe(tx -> System.out.println(tx.getAmount())); // Outputs: 100, 200, 300, 400

		Flux<Transaction> transactions3 = Flux.just(new Transaction(100), new Transaction(200), new Transaction(300));
		Mono<List<Transaction>> transactionList = transactions.collectList();
		transactionList.subscribe(list -> System.out.println("Collected " + list.size() + " transactions")); // Outputs: Collected 3 transactions

		Flux<Transaction> transactions4 = Flux.just(new Transaction(100), new Transaction(200), new Transaction(300));
		Mono<Integer> totalAmount = transactions.map(Transaction::getAmount).reduce(0, Integer::sum);
		totalAmount.subscribe(System.out::println); // Outputs: 600

		Flux<String> account1Notifications = Flux.just("Tx1: $100", "Tx2: $200");
		Flux<String> account2Notifications = Flux.just("Tx3: $300", "Tx4: $400");
		Flux<String> allNotifications = account1Notifications.mergeWith(account2Notifications);
		allNotifications.subscribe(System.out::println); // Outputs: Tx1: $100, Tx2: $200, Tx3: $300, Tx4: $400


		Flux<Transaction> day1Transactions = Flux.just(new Transaction(100), new Transaction(200));
		Flux<Transaction> day2Transactions = Flux.just(new Transaction(300), new Transaction(400));
		Flux<Transaction> allTransactions2 = day1Transactions.concatWith(day2Transactions);
		allTransactions.subscribe(tx -> System.out.println(tx.getAmount())); // Outputs: 100, 200, 300, 400

		Flux<Transaction> transactions5 = Flux.empty();
		Flux<Transaction> transactionsWithDefault = transactions.switchIfEmpty(Flux.just(new Transaction(0)));
		transactionsWithDefault.subscribe(tx -> System.out.println(tx.getAmount())); // Outputs: 0


		Flux<Transaction> transactions6 = Flux.just(new Transaction(100), new Transaction(200), new Transaction(300), new Transaction(400), new Transaction(500));
		Flux<Transaction> firstThreeTransactions = transactions.take(3);
		firstThreeTransactions.subscribe(tx -> System.out.println(tx.getAmount())); // Outputs: 100, 200, 300

		Flux<Transaction> transactions7 = Flux.just(new Transaction(100), new Transaction(200), new Transaction(300), new Transaction(400), new Transaction(500));
		Flux<Transaction> lastTwoTransactions = transactions.takeLast(2);
		lastTwoTransactions.subscribe(tx -> System.out.println(tx.getAmount())); // Outputs: 400, 500


		Flux<Transaction> transactions8 = Flux.just(new Transaction(100), new Transaction(200), new Transaction(300), new Transaction(400), new Transaction(500));
		Flux<Transaction> remainingTransactions = transactions.skip(3);
		remainingTransactions.subscribe(tx -> System.out.println(tx.getAmount())); // Outputs: 400, 500

		Flux<Transaction> transactions9 = Flux.just(new Transaction(100), new Transaction(200), new Transaction(300), new Transaction(400), new Transaction(500));
		Flux<Transaction> initialTransactions = transactions.skipLast(2);
		initialTransactions.subscribe(tx -> System.out.println(tx.getAmount())); // Outputs: 100, 200, 300

		Mono<String> customerInfo = Mono.fromCallable(() -> getCustomerInfo(12345));
		customerInfo.subscribe(System.out::println); // Outputs: Customer information

	}


	private static Mono<String> getTransactionDetails(Transaction tx) {
		// Supongamos que esto hace una llamada asíncrona para obtener detalles
		return Mono.just("Details for transaction: " + tx.getAmount());
	}

	private static String getCustomerInfo(int customerId) {
		// Supongamos que esto hace una llamada para obtener información del cliente
		return "Customer info for ID: " + customerId;
	}
}
