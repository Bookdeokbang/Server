package com.example.gachon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableJpaAuditing
@EnableMongoRepositories
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class GachonApplication {

	public static void main(String[] args) {
		SpringApplication.run(GachonApplication.class, args);
	}

}
