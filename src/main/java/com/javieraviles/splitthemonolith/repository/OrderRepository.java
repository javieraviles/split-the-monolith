package com.javieraviles.splitthemonolith.repository;

import com.javieraviles.splitthemonolith.entity.Order;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "orders", path = "orders")
public interface OrderRepository extends CrudRepository<Order, Long> {
}
