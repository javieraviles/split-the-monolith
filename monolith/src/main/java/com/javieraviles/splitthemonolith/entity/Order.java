package com.javieraviles.splitthemonolith.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "customer")
	private Customer customer;

	@Positive
	private double totalCost;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product")
	private Product product;

	@Positive
	private int productQuantity;

	public Order() {
	}

	public Order(final Customer customer, final double totalCost, final Product product, final int productQuantity) {
		this.customer = customer;
		this.totalCost = totalCost;
		this.product = product;
		this.productQuantity = productQuantity;
	}

	public long getId() {
		return id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(final double totalCost) {
		this.totalCost = totalCost;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(final Product product) {
		this.product = product;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(final int productQuantity) {
		this.productQuantity = productQuantity;
	}
}