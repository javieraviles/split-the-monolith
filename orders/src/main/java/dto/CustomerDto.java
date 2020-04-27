package dto;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public class CustomerDto {

	private long id;

	@Size(min = 3, max = 20)
	private String name;

	@PositiveOrZero
	private double credit;

	public CustomerDto() {
	}

	public boolean hasEnoughCredit(final double quantity) {
		return this.credit >= quantity;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(final double credit) {
		this.credit = credit;
	}
}