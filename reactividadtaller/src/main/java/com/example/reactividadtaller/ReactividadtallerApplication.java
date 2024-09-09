package com.example.reactividadtaller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;


@EnableReactiveMongoRepositories
@SpringBootApplication
public class ReactividadtallerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactividadtallerApplication.class, args);
	}

}
