package com.javieraviles.ordersms.saga;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javieraviles.ordersms.entity.Order;
import com.javieraviles.ordersms.exception.NotEnoughCreditException;
import com.javieraviles.ordersms.exception.NotEnoughStockException;
import com.javieraviles.ordersms.exception.ResourceNotFoundException;
import com.javieraviles.ordersms.repository.OrderRepository;

import dto.CustomerDto;
import dto.ProductDto;

@Component
public class OrderSaga {

	@Autowired
	private OrderRepository orderRepository;

	final private String monolithBaseUri = "http://localhost:8080/";

	@Autowired
	private RestTemplate restTemplate;

	public Order createOrderSaga(final Order newOrder) throws JsonProcessingException {
		ProductDto product = null;
		CustomerDto customer = null;
		try {
			product = fetchProduct(newOrder);
			customer = fetchCustomer(newOrder);
		} catch (final ResourceNotFoundException e) {
			throw e;
		}
		if (!product.hasEnoughStock(newOrder.getProductQuantity())) {
			throw new NotEnoughStockException();
		}
		if (!customer.hasEnoughCredit(newOrder.getTotalCost())) {
			throw new NotEnoughCreditException();
		}
		try {
			modifyProductStock(product.getId(), "DEDUCT", newOrder.getProductQuantity());
			modifyCustomerCredit(customer.getId(), "DEDUCT", newOrder.getTotalCost());
		} catch (final NotEnoughCreditException e) {
			/*
			 * here there is no transaction as before in the monolith, therefore can happen
			 * that after checking there is enough customer credit, when actually deducting
			 * the credit from the monolith, another order took place and there is no credit
			 * anymore. For such scenario, a NotEnoughCreditException will be thrown; the
			 * problem is we have to do the saga compensation for stock, as it was already
			 * deducted from monolith's database
			 */
			modifyProductStock(product.getId(), "ADD", newOrder.getProductQuantity());
			// of course after performing the compensation, rethrow the business exeption
			throw e;

		} catch (final RestClientException e) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Product/Customer service not available",
					e.getCause());
		}
		return orderRepository.save(newOrder);
	}

	private CustomerDto fetchCustomer(final Order newOrder) {
		return restTemplate.getForObject(monolithBaseUri + "customers/" + newOrder.getCustomerId(), CustomerDto.class);
	}

	private ProductDto fetchProduct(final Order newOrder) {
		return restTemplate.getForObject(monolithBaseUri + "products/" + newOrder.getProductId(), ProductDto.class);
	}

	private void modifyCustomerCredit(final long customerId, final String operation, final double totalCost)
			throws RestClientException, JsonProcessingException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final Map<String, String> creditUpdate = new HashMap<>();
		creditUpdate.put("operation", operation);
		creditUpdate.put("amount", String.valueOf(totalCost));
		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(creditUpdate, headers);

		restTemplate.exchange(monolithBaseUri + "customers/" + customerId, HttpMethod.PATCH, requestEntity,
				CustomerDto.class);

	}

	private void modifyProductStock(final long productId, final String operation, final int productQuantity)
			throws RestClientException, JsonProcessingException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final Map<String, String> stockUpdate = new HashMap<>();
		stockUpdate.put("operation", operation);
		stockUpdate.put("amount", String.valueOf(productQuantity));
		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(stockUpdate, headers);

		restTemplate.exchange(monolithBaseUri + "products/" + productId, HttpMethod.PATCH, requestEntity,
				ProductDto.class);
	}
}