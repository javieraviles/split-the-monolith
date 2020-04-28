package com.javieraviles.splitthemonolith.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javieraviles.splitthemonolith.dto.NotificationDto;
import com.javieraviles.splitthemonolith.dto.OperationEnum;
import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.exception.ResourceNotFoundException;
import com.javieraviles.splitthemonolith.repository.CustomerRepository;
import com.javieraviles.splitthemonolith.restclient.NotificationsMicroserviceClient;
import com.javieraviles.splitthemonolith.service.NotificationService;

@RestController
class CustomerController {

	@Value(value = "${use.notification.service}")
	private boolean useNotificationService;

	@Autowired
	private CustomerRepository repository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationsMicroserviceClient notificationsMsClient;

	@GetMapping("/customers")
	List<Customer> getAll() {
		return repository.findAll();
	}

	@PostMapping("/customers")
	ResponseEntity<Customer> createCustomer(@RequestBody Customer newCustomer) {
		return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(newCustomer));
	}

	@GetMapping("/customers/{id}")
	ResponseEntity<Customer> getOne(@PathVariable Long id) {
		final Customer customer = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		return ResponseEntity.ok(customer);
	}

	@PutMapping("/customers/{id}")
	ResponseEntity<Customer> updateCustomer(@RequestBody Customer updatedCustomer, @PathVariable Long id) {
		final Customer customer = repository.findById(id).map(c -> {
			c.setName(updatedCustomer.getName());
			c.setCredit(updatedCustomer.getCredit());
			return repository.save(c);
		}).orElseThrow(() -> new ResourceNotFoundException());
		return ResponseEntity.ok(customer);
	}

	@RequestMapping(value = "/customers/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> partialUpdateGeneric(@RequestBody Map<String, String> creditUpdate,
			@PathVariable("id") Long id) {
		try {
			final Customer customer = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
			final double creditQuantity = Double.valueOf(creditUpdate.get("amount"));
			final OperationEnum operation = OperationEnum.valueOf(creditUpdate.get("operation"));
			if (operation == OperationEnum.ADD) {
				customer.addCredit(creditQuantity);
				/*
				 * notify customer some credit was added; in the real world we would pass an
				 * email, not the customer name, but that data was not included in the model
				 */
				final NotificationDto notification = new NotificationDto(customer.getName(), creditQuantity);
				if (useNotificationService) {
					notificationsMsClient.sendNotification(notification);
				} else {
					notificationService.sendEmailNotification(notification);
				}
			} else {
				customer.deductCredit(creditQuantity);
			}
			return ResponseEntity.ok(repository.save(customer));
		} catch (final IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("wrong operation");
		}
	}

	@DeleteMapping("/customers/{id}")
	void deleteCustomer(@PathVariable Long id) {
		repository.deleteById(id);
	}
}