package com.javieraviles.splitthemonolith.controller;

import java.util.List;

import com.javieraviles.splitthemonolith.entity.Order;
import com.javieraviles.splitthemonolith.exception.ResourceNotFoundException;
import com.javieraviles.splitthemonolith.repository.OrderRepository;
import com.javieraviles.splitthemonolith.saga.OrderSaga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OrderController {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderSaga orderSaga;

    @GetMapping("/orders")
    List<Order> getAll() {
        return repository.findAll();
    }

    @PostMapping("/orders")
    ResponseEntity<Order> createOrder(@RequestBody Order newOrder) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderSaga.createOrderSaga(newOrder));
    }

    @GetMapping("/orders/{id}")
    ResponseEntity<Order> getOne(@PathVariable Long id) {
        final Order order = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/orders/{id}")
    void deleteOrder(@PathVariable Long id) {
        repository.deleteById(id);
    }
}