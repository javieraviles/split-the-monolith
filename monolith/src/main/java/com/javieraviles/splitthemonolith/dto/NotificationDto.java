package com.javieraviles.splitthemonolith.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;

public class NotificationDto {

	@Email
	private String customerEmail;

	@Positive
	private double credit;

	public NotificationDto() {
	}

	public NotificationDto(final @Email String customerEmail, final @Positive double credit) {
		this.customerEmail = customerEmail;
		this.credit = credit;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(final String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(final double credit) {
		this.credit = credit;
	}

}