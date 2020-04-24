package com.javieraviles.splitthemonolith.repository;

import com.javieraviles.splitthemonolith.entity.Customer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "customers", path = "customers")
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
