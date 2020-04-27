package dto;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public class ProductDto {

	private long id;

	@Size(min = 3, max = 20)
	private String name;

	@PositiveOrZero
	private int stock;

	public ProductDto() {
	}

	public boolean hasEnoughStock(final int quantity) {
		return this.stock >= quantity;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(final int stock) {
		this.stock = stock;
	}
}