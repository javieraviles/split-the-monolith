package com.javieraviles.splitthemonolith.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.javieraviles.splitthemonolith.exception.NotEnoughCreditException;

@Entity(name = "customers")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Size(min = 3, max = 20)
	private String name;

	@PositiveOrZero
	private double credit;

	public Customer() {
	}

	public Customer(final String name, final double credit) {
		this.name = name;
		this.credit = credit;
	}

	public void addCredit(final double quantity) {
		this.credit += quantity;
	}

	public void deductCredit(final double quantity) {
		if (quantity > this.credit) {
			throw new NotEnoughCreditException();
		}
		this.credit -= quantity;
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

	public double getCredit() {
		return credit;
	}

	public void setCredit(final double credit) {
		this.credit = credit;
	}
}