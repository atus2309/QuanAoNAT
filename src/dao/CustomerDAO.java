/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import model.Customer;
import utils.Database;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {
   private Customer readFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("full_name"),
            rs.getString("phone_number"),
            rs.getString("address")
        );
    }

    public void insert(Customer customer) {
        String sql = "INSERT INTO Customers (full_name, phone_number, address) VALUES (?, ?, ?)";
        Database.update(sql, customer.getFullName(), customer.getPhoneNumber(), customer.getAddress());
    }

    public void update(Customer customer) {
        String sql = "UPDATE Customers SET full_name = ?, phone_number = ?, address = ? WHERE customer_id = ?";
        Database.update(sql, customer.getFullName(), customer.getPhoneNumber(), customer.getAddress(), customer.getCustomerId());
    }

    public void delete(int customerId) {
        String sql = "DELETE FROM Customers WHERE customer_id = ?";
        Database.update(sql, customerId);
    }

    public List<Customer> selectAll() {
        String sql = "SELECT * FROM Customers";
        return select(sql);
    }

    public Customer selectById(int customerId) {
        String sql = "SELECT * FROM Customers WHERE customer_id = ?";
        List<Customer> list = select(sql, customerId);
        return list.isEmpty() ? null : list.get(0);
    }
    
    // Phương thức select chung để tránh lặp code
    private List<Customer> select(String sql, Object... args) {
        List<Customer> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu Customer", e);
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
