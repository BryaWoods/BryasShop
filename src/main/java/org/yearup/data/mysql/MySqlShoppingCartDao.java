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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void addCartItem(int userId, int productId, int quantity) {
        String sql = "INSERT INTO shopping_cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        String deleteCartSql = "DELETE FROM shopping_cart WHERE user_id = ?";
        String deleteCartItemsSql = "DELETE FROM shopping_cart_items WHERE user_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement psCart = conn.prepareStatement(deleteCartSql);
                 PreparedStatement psCartItems = conn.prepareStatement(deleteCartItemsSql)) {

                psCart.setInt(1, userId);
                psCartItems.setInt(1, userId);

                psCart.executeUpdate();
                psCartItems.executeUpdate();

                conn.commit(); // Commit transaction if both deletes are successful

            } catch (Exception e) {
                conn.rollback(); // Rollback transaction if there is an error
                throw new RuntimeException("Error clearing cart", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error establishing connection", e);
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

    @Override
    public List<ShoppingCartItem> getCartItemsByUserId(int userId) {
        String sql = "SELECT sci.product_id, sci.quantity, p.price "
                + "FROM shopping_cart_items sci "
                + "JOIN products p ON sci.product_id = p.product_id "
                + "WHERE sci.user_id = ?";
        List<ShoppingCartItem> cartItems = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProductId(resultSet.getInt("product_id"));
                    item.setQuantity(resultSet.getInt("quantity"));

                    // Create and set a new product with the price
                    Product product = new Product();
                    product.setProductId(resultSet.getInt("product_id"));
                    product.setPrice(resultSet.getBigDecimal("price"));
                    item.setProduct(product);

                    cartItems.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cartItems;
    }


    @Override
    public void clearCartByUserId(int userId) {

    }

    @Override
    public ShoppingCartItem getShoppingCartItemByProductId(int productId) {
        String sql = "SELECT * FROM shopping_cart WHERE product_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProductId(productId); // Set the product ID
                    item.setQuantity(resultSet.getInt("quantity"));
                    // Populate other fields as needed, e.g., price, discount, etc.
                    return item;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving shopping cart item by productId: " + productId, e);
        }
        return null; // Return null if no item found for the given productId
    }

}

