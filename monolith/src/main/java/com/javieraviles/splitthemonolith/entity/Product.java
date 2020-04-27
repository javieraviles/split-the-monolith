package com.javieraviles.splitthemonolith.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.javieraviles.splitthemonolith.exception.NotEnoughStockException;

@Entity(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Size(min = 3, max = 20)
	private String name;

	@PositiveOrZero
	private int stock;

	public Product() {
	}

	public Product(final String name, final int stock) {
		this.name = name;
		this.stock = stock;
	}

	public void addStock(final int quantity) {
		this.stock += quantity;
	}

	public void deductStock(final int quantity) {
		if (quantity > this.stock) {
			throw new NotEnoughStockException();
		}
		this.stock -= quantity;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(final int stock) {
		this.stock = stock;
	}
}