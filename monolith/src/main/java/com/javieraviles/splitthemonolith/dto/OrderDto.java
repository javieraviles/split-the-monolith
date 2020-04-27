package com.javieraviles.splitthemonolith.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class OrderDto {

	private long id;

	@NotNull
	private long customerId;

	@Positive
	private double totalCost;

	@NotNull
	private long productId;

	@Positive
	private int productQuantity;

	public OrderDto() {
	}

	public OrderDto(long id, @NotNull long customerId, @Positive double totalCost, @NotNull long productId,
			@Positive int productQuantity) {
		this.id = id;
		this.customerId = customerId;
		this.totalCost = totalCost;
		this.productId = productId;
		this.productQuantity = productQuantity;
	}

	public long getId() {
		return id;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(final double totalCost) {
		this.totalCost = totalCost;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(final int productQuantity) {
		this.productQuantity = productQuantity;
	}
}