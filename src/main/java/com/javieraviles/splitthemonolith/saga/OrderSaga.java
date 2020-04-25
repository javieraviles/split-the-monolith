package com.javieraviles.splitthemonolith.saga;

import com.javieraviles.splitthemonolith.entity.Customer;
import com.javieraviles.splitthemonolith.entity.Order;
import com.javieraviles.splitthemonolith.entity.Product;
import com.javieraviles.splitthemonolith.exception.NotEnoughCreditException;
import com.javieraviles.splitthemonolith.exception.NotEnoughStockException;
import com.javieraviles.splitthemonolith.exception.ResourceNotFoundException;
import com.javieraviles.splitthemonolith.repository.CustomerRepository;
import com.javieraviles.splitthemonolith.repository.OrderRepository;
import com.javieraviles.splitthemonolith.repository.ProductRepository;
import com.javieraviles.splitthemonolith.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderSaga {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;

    public Order createOrderSaga(Order newOrder) {
        final Product product = productRepository.findById(newOrder.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException());
        if (!product.hasEnoughStock(newOrder.getProductQuantity())) {
            throw new NotEnoughStockException();
        }
        newOrder.setProduct(product);
        final Customer customer = customerRepository.findById(newOrder.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException());
        if (!customer.hasEnoughCredit(newOrder.getTotalCost())) {
            throw new NotEnoughCreditException();
        }
        newOrder.setCustomer(customer);

        product.decreaseStock(newOrder.getProductQuantity());
        customer.deductCredit(newOrder.getTotalCost());

        final Order createdOrder = orderRepository.save(newOrder);

        // notify the customer via email -> order created!
        notificationService.sendEmailNotification(createdOrder);
        return createdOrder;
    }
}