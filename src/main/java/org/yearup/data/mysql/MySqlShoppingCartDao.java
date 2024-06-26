package org.yearup.data.mysql;


import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    private final ProductDao productDao;

    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getCartByUserId(int userId) {
        String sql = "SELECT sc.product_id, sc.quantity, p.product_id, p.name, p.price, p.category_id, p.description, " +
                "p.color, p.stock, p.image_url, p.featured " +
                "FROM shopping_cart sc " +
                "JOIN products p ON sc.product_id = p.product_id " +
                "WHERE sc.user_id = ?";

        ShoppingCart cart = new ShoppingCart();
        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int quantity = rs.getInt("quantity");

                    Product product = new Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setDescription(rs.getString("description"));
                    product.setColor(rs.getString("color"));
                    product.setStock(rs.getInt("stock"));
                    product.setImageUrl(rs.getString("image_url"));
                    product.setFeatured(rs.getBoolean("featured"));

                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(quantity);

                    items.put(productId, item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving shopping cart", e);
        }

        cart.setItems(items);
        return cart;
    }

    @Override
    public void addProductToCart(int userId, int productId) {
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error adding product to cart", e);
        }
    }

    @Override
    public void updateProductInCart(int userId, int productId, int quantity) {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating product in cart", e);
        }
    }

    @Override
    public void clearCart(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error clearing cart", e);
        }
    }

    @Override
    public void updateProductQuantity(int userId, int productId, int quantity) {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating product quantity in cart", e);
        }
    }

}

