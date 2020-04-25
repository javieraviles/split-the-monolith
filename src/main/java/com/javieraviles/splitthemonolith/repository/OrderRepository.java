package com.javieraviles.splitthemonolith.repository;

import com.javieraviles.splitthemonolith.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
