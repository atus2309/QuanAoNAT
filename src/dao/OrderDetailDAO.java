/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import model.OrderDetail;
import utils.Database;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDetailDAO {
    private OrderDetail readFromResultSet(ResultSet rs) throws SQLException {
        return new OrderDetail(
            rs.getInt("order_detail_id"),
            rs.getInt("order_id"),
            rs.getInt("variant_id"),
            rs.getInt("quantity"),
            rs.getBigDecimal("price_per_unit")
        );
    }
    
    public void insert(OrderDetail detail) {
        String sql = "INSERT INTO OrderDetails (order_id, variant_id, quantity, price_per_unit) VALUES (?, ?, ?, ?)";
        Database.update(sql, detail.getOrderId(), detail.getVariantId(), detail.getQuantity(), detail.getPricePerUnit());
    }

    public List<OrderDetail> selectByOrderId(int orderId) {
        String sql = "SELECT * FROM OrderDetails WHERE order_id = ?";
        List<OrderDetail> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, orderId);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu OrderDetail", e);
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
