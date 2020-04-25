package com.javieraviles.splitthemonolith;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.entity.Order;
import com.javieraviles.splitthemonolith.entity.Product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void givenOneCustomer_whenGetCustomers_thenReturnJsonArray() throws Exception {
		mvc.perform(get("/customers").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is("Javier Aviles")));
	}

	@Test
	public void givenOneProduct_whenGetProducts_thenReturnJsonArray() throws Exception {
		mvc.perform(get("/products").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is("Chair")));
	}

	@Test
	public void whenCreateOrder_thenReturnCreated() throws Exception {
		final Customer maria = new Customer("Maria Frutos", 1200.21);
		final Product superChair = new Product("Super Chair", 40);

		MvcResult resultCustomer = mvc.perform(post("/customers").content(asJsonString(maria))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();

		MvcResult resultProduct = mvc.perform(post("/products").content(asJsonString(superChair))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();

		final Order newOrder = new Order(
				objectFromJson(resultCustomer.getResponse().getContentAsString(), Customer.class), 120.35,
				objectFromJson(resultProduct.getResponse().getContentAsString(), Product.class), 1);

		mvc.perform(post("/orders").content(asJsonString(newOrder)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
	}

	@Test
	public void whenCreateOrder_withoutEnoughStock_thenReturnBadRequestWithMessage() throws Exception {
		final Customer maria = new Customer("Maria Frutos", 1200.21);
		final Product superChair = new Product("Super Chair", 4);

		MvcResult resultCustomer = mvc.perform(post("/customers").content(asJsonString(maria))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();

		MvcResult resultProduct = mvc.perform(post("/products").content(asJsonString(superChair))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();

		final Order newOrder = new Order(
				objectFromJson(resultCustomer.getResponse().getContentAsString(), Customer.class), 962.33,
				objectFromJson(resultProduct.getResponse().getContentAsString(), Product.class), 8);

		mvc.perform(post("/orders").content(asJsonString(newOrder)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(status().reason(containsString("Not enough stock")));
	}

	@Test
	public void whenCreateOrder_withoutEnoughCredit_thenReturnBadRequestWithMessage() throws Exception {
		final Customer maria = new Customer("Maria Frutos", 1200.21);
		final Product superChair = new Product("Super Chair", 40);

		MvcResult resultCustomer = mvc.perform(post("/customers").content(asJsonString(maria))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();

		MvcResult resultProduct = mvc.perform(post("/products").content(asJsonString(superChair))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();

		final Order newOrder = new Order(
				objectFromJson(resultCustomer.getResponse().getContentAsString(), Customer.class), 1444.2,
				objectFromJson(resultProduct.getResponse().getContentAsString(), Product.class), 12);

		mvc.perform(post("/orders").content(asJsonString(newOrder)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(status().reason(containsString("Not enough credit")));
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T objectFromJson(String json, Class<T> attributeClass)
			throws JsonMappingException, JsonProcessingException {
		return new ObjectMapper().readValue(json, attributeClass);
	}

}
