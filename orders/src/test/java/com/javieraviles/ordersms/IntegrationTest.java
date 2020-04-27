package com.javieraviles.ordersms;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javieraviles.ordersms.entity.Order;

import dto.CustomerDto;
import dto.ProductDto;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private RestTemplate restTemplate;

	private MockRestServiceServer mockServer;
	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void whenReproducingCreateOrderSaga_thenReturnCreated() throws Exception {
		mockServer = MockRestServiceServer.createServer(restTemplate);

		CustomerDto customer = new CustomerDto();
		customer.setCredit(1500.17);
		customer.setName("Javier Aviles");
		customer.setId(1);

		mockServer
				.expect(ExpectedCount.once(), requestTo(new URI("http://localhost:8080/customers/" + customer.getId())))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customer)));

		ProductDto product = new ProductDto();
		product.setStock(12);
		product.setName("Chair");
		product.setId(2);

		mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://localhost:8080/products/" + product.getId())))
				.andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(product)));

		final Order newOrder = new Order(1, 120.35, 2, 1);

		product.setStock(11);

		mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://localhost:8080/products/" + product.getId())))
				.andExpect(method(HttpMethod.PATCH)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(product)));

		customer.setCredit(1379.82);

		mockServer
				.expect(ExpectedCount.once(), requestTo(new URI("http://localhost:8080/customers/" + customer.getId())))
				.andExpect(method(HttpMethod.PATCH)).andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customer)));

		mvc.perform(post("/orders").content(asJsonString(newOrder)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
