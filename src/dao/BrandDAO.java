/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import model.Brand;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.Database;

public class BrandDAO {
    private Brand readFromResultSet(ResultSet rs) throws SQLException {
        return new Brand(
            rs.getInt("brand_id"),
            rs.getString("brand_name")
        );
    }
    public void insert(Brand brand) {
        String sql = "INSERT INTO Brands (brand_name) VALUES (?)";
        Database.update(sql, brand.getBrandName());
    }

    
    public void update(Brand brand) {
        String sql = "UPDATE Brands SET brand_name = ? WHERE brand_id = ?";
        Database.update(sql, brand.getBrandName(), brand.getBrandId());
    }

    
    public void delete(int brandId) {
        String sql = "DELETE FROM Brands WHERE brand_id = ?";
        Database.update(sql, brandId);
    }
    public List<Brand> selectAll() {
        String sql = "SELECT * FROM Brands";
        List<Brand> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu Brand", e);
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
    
    public Brand selectById(int brandId) {
        String sql = "SELECT * FROM Brands WHERE brand_id = ?";
        List<Brand> list = new ArrayList<>();
         ResultSet rs = null;
        try {
            rs = Database.query(sql, brandId);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(rs);
        }
        return list.isEmpty() ? null : list.get(0);
    }
    
    
    private void closeConnection(ResultSet rs) {
        if (rs != null) {
            try {
                rs.getStatement().getConnection().close();
            } catch (SQLException e) {
                // Bỏ qua lỗi khi đóng
            }
        }
    }
}
