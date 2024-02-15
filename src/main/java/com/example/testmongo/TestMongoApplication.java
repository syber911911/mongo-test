package com.example.testmongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class TestMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestMongoApplication.class, args);
	}
}
