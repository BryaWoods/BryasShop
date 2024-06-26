package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/cart")
@PreAuthorize("hasRole('ROLE_USER')")
public class ShoppingCartController {
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }
            return shoppingCartDao.getCartByUserId(user.getId());
        } catch (Exception e) {
            e.printStackTrace(); // Add this line for debugging
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }

    @PostMapping("/products/{productId}")
    public ShoppingCart addProductToCart(Principal principal, @PathVariable int productId) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }
            System.out.println("Adding product to cart: User ID: " + user.getId() + ", Product ID: " + productId); // Debug statement
            shoppingCartDao.addProductToCart(user.getId(), productId);
            return shoppingCartDao.getCartByUserId(user.getId());
        } catch (Exception e) {
            e.printStackTrace(); // Add this line for debugging
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }

    @PutMapping("/products/{productId}")
    public ShoppingCart updateProductInCart(Principal principal, @PathVariable int productId, @RequestBody ShoppingCartItem item) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }
            System.out.println("Updating product in cart: User ID: " + user.getId() + ", Product ID: " + productId + ", Quantity: " + item.getQuantity()); // Debug statement
            shoppingCartDao.updateProductInCart(user.getId(), productId, item.getQuantity());
            return shoppingCartDao.getCartByUserId(user.getId());
        } catch (Exception e) {
            e.printStackTrace(); // Add this line for debugging
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }

    @DeleteMapping
    public void clearCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }
            System.out.println("Clearing cart for user: " + user.getId()); // Debug statement
            shoppingCartDao.clearCart(user.getId());
        } catch (Exception e) {
            e.printStackTrace(); // Add this line for debugging
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }

    @PutMapping("/{productId}/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateProductQuantityInCart(@PathVariable int productId, @RequestBody Map<String, Integer> body, Principal principal) {
        int userId = Integer.parseInt(principal.getName()); // assuming principal name is user ID
        int quantity = body.get("quantity");

        ShoppingCart cart = shoppingCartDao.getCartByUserId(userId);
        if (cart.contains(productId)) {
            shoppingCartDao.updateProductQuantity(userId, productId, quantity);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not in cart");
        }
    }
}
