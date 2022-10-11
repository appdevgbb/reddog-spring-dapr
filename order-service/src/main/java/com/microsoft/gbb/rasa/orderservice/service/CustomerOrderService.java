package com.microsoft.gbb.rasa.orderservice.service;

import com.microsoft.gbb.rasa.orderservice.dto.CustomerOrderDto;
import com.microsoft.gbb.rasa.orderservice.dto.OrderItemSummaryDto;
import com.microsoft.gbb.rasa.orderservice.dto.OrderSummaryDto;
import com.microsoft.gbb.rasa.orderservice.messaging.DaprPublisher;
import com.microsoft.gbb.rasa.orderservice.model.Product;
import com.microsoft.gbb.rasa.orderservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Create new orders for customers.
 */
@Slf4j
@Service
@Transactional
@Qualifier("customerorder")
public class CustomerOrderService implements OrderService {

    private final ProductRepository productRespository;
    private final DaprPublisher daprPublisher;

    public CustomerOrderService(DaprPublisher daprPublisher, ProductRepository productRepository) {
        this.daprPublisher = daprPublisher;
        this.productRespository = productRepository;
    }

    /**
     * Create order for customer.
     *
     * @param order the order
     * @return the string
     */
    public OrderSummaryDto createOrder(CustomerOrderDto order) {
        log.info("Creating order");
        var orderSummary = getOrderSummary(order);
        daprPublisher.publishEvent(orderSummary);
        return orderSummary;
    }

    public OrderSummaryDto getOrderSummary(CustomerOrderDto order) {

        // Retrieve all the items
        ArrayList<Product> products = productRespository.getAllProducts();

        // Iterate through the list of ordered items to calculate
        // the total and compile a list of item summaries.
        AtomicReference<Float> orderTotal = new AtomicReference<>(0.0f);
        var itemSummaries = new ArrayList<OrderItemSummaryDto>();
        order.getOrderItems().forEach(orderItem -> {
            Product product = products.stream()
                    .filter((p) -> p.getProductId() == orderItem.getId())
                    .findFirst().orElse(null);
            if (product == null) return;

            orderTotal.updateAndGet(v -> (float) (v + (product.getUnitPrice() * orderItem.getQuantity())));
            itemSummaries.add(OrderItemSummaryDto.builder()
                    .productId(Math.toIntExact(orderItem.getId()))
                    .productName(product.getProductName())
                    .unitPrice(product.getUnitPrice())
                    .quantity(orderItem.getQuantity())
                    .unitCost(product.getUnitCost())
                    .imageUrl(product.getImageUrl())
                    .build());
        });

        // return initialized order summary
        return OrderSummaryDto.builder()
                .orderId(String.valueOf(UUID.randomUUID()))
                .storeId(order.getStoreId())
                .firstName(order.getFirstName())
                .lastName(order.getLastName())
                .loyaltyId(order.getLoyaltyId())
                .orderDate(LocalDateTime.now(Clock.systemUTC()))
                .orderItems(itemSummaries)
                .orderTotal(BigDecimal.valueOf(orderTotal.get())
                        .setScale(2, RoundingMode.HALF_DOWN)
                        .doubleValue())
                .build();
    }
}

