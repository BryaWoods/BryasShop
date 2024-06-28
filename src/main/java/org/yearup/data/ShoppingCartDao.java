package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartDao {
    ShoppingCart getCartByUserId(int userId);
    void addProductToCart(int userId, int productId);
    void addCartItem(int userId, int productId, int quantity);
    void updateProductInCart(int userId, int productId, int quantity);
    void clearCart(int userId);
    void updateProductQuantity(int userId, int productId, int quantity);
    List<ShoppingCartItem> getCartItemsByUserId(int userId);
    void clearCartByUserId(int userId);
    ShoppingCartItem getShoppingCartItemByProductId(int productId);
}





