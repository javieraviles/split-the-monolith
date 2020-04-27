package com.javieraviles.splitthemonolith.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.entity.Order;
import com.javieraviles.splitthemonolith.entity.Product;
import com.javieraviles.splitthemonolith.exception.ResourceNotFoundException;
import com.javieraviles.splitthemonolith.repository.CustomerRepository;
import com.javieraviles.splitthemonolith.repository.OrderRepository;
import com.javieraviles.splitthemonolith.repository.ProductRepository;

@Component
public class OrderSaga {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Transactional
	public Order createOrderSaga(Order newOrder) {
		final Product product = productRepository.findById(newOrder.getProduct().getId())
				.orElseThrow(() -> new ResourceNotFoundException());
		newOrder.setProduct(product);
		final Customer customer = customerRepository.findById(newOrder.getCustomer().getId())
				.orElseThrow(() -> new ResourceNotFoundException());
		newOrder.setCustomer(customer);

		product.deductStock(newOrder.getProductQuantity());
		/*
		 * this is all part of one transaction due to @Transactional annotation So no
		 * need to do a saga compensation as credit will only be deducted in case the
		 * product had enough stock
		 */
		customer.deductCredit(newOrder.getTotalCost());

		return orderRepository.save(newOrder);
	}
}