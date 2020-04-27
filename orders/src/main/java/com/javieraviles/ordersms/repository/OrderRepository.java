package com.javieraviles.ordersms.repository;

import com.javieraviles.ordersms.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
