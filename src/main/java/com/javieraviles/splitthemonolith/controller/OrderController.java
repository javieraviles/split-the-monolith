package com.javieraviles.splitthemonolith.controller;

import java.util.List;

import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.entity.Order;
import com.javieraviles.splitthemonolith.entity.Product;
import com.javieraviles.splitthemonolith.exception.NotEnoughCreditException;
import com.javieraviles.splitthemonolith.exception.NotEnoughStockException;
import com.javieraviles.splitthemonolith.exception.ResourceNotFoundException;
import com.javieraviles.splitthemonolith.repository.CustomerRepository;
import com.javieraviles.splitthemonolith.repository.OrderRepository;
import com.javieraviles.splitthemonolith.repository.ProductRepository;

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
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/orders")
    List<Order> getAll() {
        return repository.findAll();
    }

    @PostMapping("/orders")
    ResponseEntity<Order> createOrder(@RequestBody Order newOrder) {
        final Product product = productRepository.findById(newOrder.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException());
        if (product.getStock() < newOrder.getProductQuantity()) {
            throw new NotEnoughStockException();
        }
        newOrder.setProduct(product);
        final Customer customer = customerRepository.findById(newOrder.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException());
        if (customer.getCredit() < newOrder.getTotalCost()) {
            throw new NotEnoughCreditException();
        }
        newOrder.setCustomer(customer);

        product.setStock(product.getStock() - newOrder.getProductQuantity());
        customer.setCredit(customer.getCredit() - newOrder.getTotalCost());

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(newOrder));
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