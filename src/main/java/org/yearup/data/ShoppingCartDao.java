package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao {
    ShoppingCart getCartByUserId(int userId);
    void addProductToCart(int userId, int productId);
    void updateProductInCart(int userId, int productId, int quantity);
    void clearCart(int userId);
    void updateProductQuantity(int userId, int productId, int quantity);
}





