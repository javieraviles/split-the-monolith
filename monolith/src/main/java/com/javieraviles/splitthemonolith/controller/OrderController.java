package com.javieraviles.splitthemonolith.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.javieraviles.splitthemonolith.dto.OrderDto;
import com.javieraviles.splitthemonolith.entity.Order;
import com.javieraviles.splitthemonolith.exception.ResourceNotFoundException;
import com.javieraviles.splitthemonolith.repository.OrderRepository;
import com.javieraviles.splitthemonolith.saga.OrderSaga;

@RestController
class OrderController {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private OrderSaga orderSaga;

	@GetMapping("/orders")
	List<OrderDto> getAll() {
		final List<Order> orders = repository.findAll();
		List<OrderDto> ordersDtos = orders.stream().map(o -> getDto(o)).collect(Collectors.toList());
		return ordersDtos;
	}

	@PostMapping("/orders")
	ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto newOrder) {
		return ResponseEntity.status(HttpStatus.CREATED).body(getDto(orderSaga.createOrderSaga(newOrder)));
	}

	@GetMapping("/orders/{id}")
	ResponseEntity<OrderDto> getOne(@PathVariable Long id) {
		final Order order = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		return ResponseEntity.ok(getDto(order));
	}

	@DeleteMapping("/orders/{id}")
	void deleteOrder(@PathVariable Long id) {
		repository.deleteById(id);
	}

	private OrderDto getDto(final Order order) {
		return new OrderDto(order.getId(), order.getCustomer().getId(), order.getTotalCost(),
				order.getProduct().getId(), order.getProductQuantity());
	}
}