package com.javieraviles.splitthemonolith.repository;

import com.javieraviles.splitthemonolith.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
