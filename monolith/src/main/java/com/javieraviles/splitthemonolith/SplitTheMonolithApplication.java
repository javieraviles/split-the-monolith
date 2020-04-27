package com.javieraviles.splitthemonolith;

import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.entity.Order;
import com.javieraviles.splitthemonolith.entity.Product;
import com.javieraviles.splitthemonolith.repository.CustomerRepository;
import com.javieraviles.splitthemonolith.repository.OrderRepository;
import com.javieraviles.splitthemonolith.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SplitTheMonolithApplication implements CommandLineRunner {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	OrderRepository orderRepository;

	public static void main(String[] args) {
		SpringApplication.run(SplitTheMonolithApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// Load some data in db
		final Customer javi = new Customer("Javier Aviles", 1500.17);
		final Product chair = new Product("Chair", 12);
		customerRepository.save(javi);
		productRepository.save(chair);
		orderRepository.save(new Order(javi, 95.17, chair, 2));
	}

}
