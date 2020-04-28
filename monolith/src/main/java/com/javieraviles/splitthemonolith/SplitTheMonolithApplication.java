package com.javieraviles.splitthemonolith;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.entity.Order;
import com.javieraviles.splitthemonolith.entity.Product;
import com.javieraviles.splitthemonolith.repository.CustomerRepository;
import com.javieraviles.splitthemonolith.repository.OrderRepository;
import com.javieraviles.splitthemonolith.repository.ProductRepository;

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

	@Bean
	RestTemplate restTemplate() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		return createRestTemplate(httpClient);
	}

	private RestTemplate createRestTemplate(final CloseableHttpClient httpClient) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}

}
