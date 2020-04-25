package com.javieraviles.splitthemonolith;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javieraviles.splitthemonolith.entity.Customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void givenOneCustomer_whenGetCustomers_thenReturnJsonArray() throws Exception {
		mvc.perform(get("/customers").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.customers", hasSize(1)))
				.andExpect(jsonPath("$._embedded.customers[0].name", is("Javier Aviles")));
	}

	@Test
	public void whenCreateCustomer_thenReturnCreated() throws Exception {
		mvc.perform(post("/customers").content(asJsonString(new Customer("Maria Frutos", 1200.21)))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	public void givenOneProduct_whenGetProducts_thenReturnJsonArray() throws Exception {
		mvc.perform(get("/products").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.products", hasSize(1)))
				.andExpect(jsonPath("$._embedded.products[0].name", is("Chair")));
	}

	@Test
	public void givenOneOrder_whenGetOrders_thenReturnJsonArray() throws Exception {
		mvc.perform(get("/orders").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.orders", hasSize(1)));
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
