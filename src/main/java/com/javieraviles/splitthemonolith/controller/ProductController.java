package com.javieraviles.splitthemonolith.controller;

import java.util.List;

import com.javieraviles.splitthemonolith.entity.Product;
import com.javieraviles.splitthemonolith.exception.ResourceNotFoundException;
import com.javieraviles.splitthemonolith.repository.ProductRepository;

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
class ProductController {

    @Autowired
    private ProductRepository repository;

    @GetMapping("/products")
    List<Product> getAll() {
        return repository.findAll();
    }

    @PostMapping("/products")
    ResponseEntity<Product> createProduct(@RequestBody Product newProduct) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(newProduct));
    }

    @GetMapping("/products/{id}")
    ResponseEntity<Product> getOne(@PathVariable Long id) {
        final Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        return ResponseEntity.ok(product);
    }

    @PutMapping("/products/{id}")
    ResponseEntity<Product> updateProduct(@RequestBody Product updatedProduct, @PathVariable Long id) {
        final Product product = repository.findById(id).map(p -> {
            p.setName(updatedProduct.getName());
            p.setStock(updatedProduct.getStock());
            return repository.save(p);
        }).orElseThrow(() -> new ResourceNotFoundException());
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/products/{id}")
    void deleteProduct(@PathVariable Long id) {
        repository.deleteById(id);
    }
}