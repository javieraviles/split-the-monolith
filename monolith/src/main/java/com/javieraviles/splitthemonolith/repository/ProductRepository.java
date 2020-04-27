package com.javieraviles.splitthemonolith.repository;

import com.javieraviles.splitthemonolith.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
