package com.javieraviles.splitthemonolith;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	public void givenEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {

		mvc.perform(get("/customers").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.customers", hasSize(1)))
				.andExpect(jsonPath("$._embedded.customers[0].name", is("Javier Aviles")));
	}
}
