/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Category;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import utils.Database;

public class CategoryDAO {
    private Category readFromResultSet(ResultSet rs) throws SQLException {
        return new Category(
            rs.getInt("category_id"),
            rs.getString("category_name")
        );
    }
    
    public void insert(Category category) {
        String sql = "INSERT INTO Categories (category_name) VALUES (?)";
        Database.update(sql, category.getCategoryName());
    }

    public void update(Category category) {
        String sql = "UPDATE Categories SET category_name = ? WHERE category_id = ?";
        Database.update(sql, category.getCategoryName(), category.getCategoryId());
    }

    public void delete(int categoryId) {
        String sql = "DELETE FROM Categories WHERE category_id = ?";
        Database.update(sql, categoryId);
    }
    
    public List<Category> selectAll() {
        String sql = "SELECT * FROM Categories";
        List<Category> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu Category", e);
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
    
    public Category selectById(int categoryId) {
        String sql = "SELECT * FROM Categories WHERE category_id = ?";
        List<Category> list = new ArrayList<>();
        ResultSet rs = null;
         try {
            rs = Database.query(sql, categoryId);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {}
            }
        }
        return list.isEmpty() ? null : list.get(0);
    }
}
