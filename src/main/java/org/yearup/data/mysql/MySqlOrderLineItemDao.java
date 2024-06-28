// MySqlOrderLineItemDao.java
package org.yearup.data.mysql;

import org.springframework.stereotype.Repository;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {

    public MySqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void create(OrderLineItem orderLineItem) {
        String sql = "INSERT INTO order_line_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderLineItem.getOrderId());
            statement.setInt(2, orderLineItem.getProductId());
            statement.setInt(3, orderLineItem.getQuantity());
            statement.setBigDecimal(4, orderLineItem.getPrice());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}



