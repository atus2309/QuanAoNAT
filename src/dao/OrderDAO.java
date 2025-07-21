/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import utils.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderDAO {
    private Order readFromResultSet(ResultSet rs) throws SQLException {
        return new Order(
            rs.getInt("order_id"),
            (Integer) rs.getObject("customer_id"),
            rs.getInt("user_id"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getBigDecimal("total_amount"),
            rs.getString("status"),
            rs.getString("payment_method")
        );
    }
    

    public int insertAndGetId(Order order) {
        String sql = "INSERT INTO Orders (customer_id, user_id, total_amount, status, payment_method) VALUES (?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();";
        Object generatedId = Database.value(sql,
                order.getCustomerId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentMethod());

        // Chuyển đổi ID trả về thành kiểu int
        if (generatedId instanceof BigDecimal) {
            return ((BigDecimal) generatedId).intValue();
        } else if (generatedId instanceof Number) {
            return ((Number) generatedId).intValue();
        }
        throw new RuntimeException("Không thể lấy ID của hóa đơn vừa tạo.");
    }
    
    public List<Order> selectAll() {
        String sql = "SELECT * FROM Orders ORDER BY created_at DESC";
        return select(sql);
    }
    
    private List<Order> select(String sql, Object... args) {
        List<Order> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu Order", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    // Bỏ qua lỗi khi đóng
                }
            }
        }
        return list;
    }
}
