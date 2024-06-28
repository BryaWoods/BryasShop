// OrdersController.java
package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.OrderDao;
import org.yearup.data.OrderLineItemDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;
import org.yearup.models.ShoppingCartItem;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/orders")
public class OrdersController {
    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;
    private final ShoppingCartDao shoppingCartDao;

    @Autowired
    public OrdersController(OrderDao orderDao, OrderLineItemDao orderLineItemDao, ShoppingCartDao shoppingCartDao) {
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.shoppingCartDao = shoppingCartDao;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(Authentication authentication) {
        int userId = 0;
        try {
            userId = getUserIdFromAuthentication(authentication);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Retrieve the current user's shopping cart
        List<ShoppingCartItem> cartItems = shoppingCartDao.getCartItemsByUserId(userId);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Create a new Order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new Date());
        orderDao.create(order);

        // Create OrderLineItem for each cart item
        for (ShoppingCartItem cartItem : cartItems) {
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setOrderId(order.getOrderId());
            orderLineItem.setProductId(cartItem.getProductId());
            orderLineItem.setQuantity(cartItem.getQuantity());
            orderLineItem.setPrice(cartItem.getLineTotal());
            orderLineItemDao.create(orderLineItem);
        }

        // Clear the shopping cart
        shoppingCartDao.clearCartByUserId(userId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private int getUserIdFromAuthentication(Authentication authentication) {
        // Implement logic to get user ID from authentication
        return 1; // Replace with actual logic
    }
}

