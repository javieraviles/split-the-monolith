package com.javieraviles.splitthemonolith.controller;

import java.util.List;

import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.exception.ResourceNotFoundException;
import com.javieraviles.splitthemonolith.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @GetMapping("/customers")
    List<Customer> getAll() {
        return repository.findAll();
    }

    @PostMapping("/customers")
    ResponseEntity<Customer> createCustomer(@RequestBody Customer newCustomer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(newCustomer));
    }

    @GetMapping("/customers/{id}")
    ResponseEntity<Customer> getOne(@PathVariable Long id) {
        final Customer customer = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/customers/{id}")
    ResponseEntity<Customer> updateCustomer(@RequestBody Customer updatedCustomer, @PathVariable Long id) {
        final Customer customer = repository.findById(id).map(c -> {
            c.setName(updatedCustomer.getName());
            c.setCredit(updatedCustomer.getCredit());
            return repository.save(c);
        }).orElseThrow(() -> new ResourceNotFoundException());
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/customers/{id}")
    void deleteCustomer(@PathVariable Long id) {
        repository.deleteById(id);
    }
}