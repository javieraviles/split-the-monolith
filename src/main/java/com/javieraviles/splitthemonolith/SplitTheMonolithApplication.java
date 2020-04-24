package com.javieraviles.splitthemonolith;

import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SplitTheMonolithApplication implements CommandLineRunner {

	@Autowired
	CustomerRepository customerRepository;

	public static void main(String[] args) {
		SpringApplication.run(SplitTheMonolithApplication.class, args);
	}

	@Override
	public void run(String... args) {
		customerRepository.save(new Customer("Javier Aviles", 1500.17));
	}

}
