/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import utils.Database;

public class ProductDAO {


    private Product readFromResultSet(ResultSet rs) throws SQLException {
        Product model = new Product();
        model.setProductId(rs.getInt("product_id"));
        model.setProductName(rs.getString("product_name"));
        model.setDescription(rs.getString("description"));
        model.setCategoryId(rs.getInt("category_id"));
        model.setBrandId(rs.getInt("brand_id"));
        model.setSeason(rs.getString("season"));
        model.setGender(rs.getString("gender"));
        return model;
    }
 private List<Product> select(String sql, Object... args) {
        List<Product> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu Product", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    // Bỏ qua lỗi khi đóng kết nối
                }
            }
        }
        return list;
    }

    public List<Object[]> selectAllProductsWithDetails() {
        String sql = """
            SELECT
                p.product_id,
                p.product_name,
                c.category_name,
                b.brand_name,
                p.season,
                p.gender,
                p.description
            FROM
                Products p
            LEFT JOIN
                Categories c ON p.category_id = c.category_id
            LEFT JOIN
                Brands b ON p.brand_id = b.brand_id
        """;
        List<Object[]> list = new ArrayList<>();
        try {
            ResultSet rs = Database.query(sql);
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    rs.getString("brand_name"),
                    rs.getString("season"),
                    rs.getString("gender"),
                    rs.getString("description")
                });
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu sản phẩm chi tiết", e);
        }
        return list;
    }

    /**
     * Thêm một sản phẩm mới
     */
    public void insert(Product product) {
        String sql = "INSERT INTO Products (product_name, description, category_id, brand_id, season, gender) VALUES (?, ?, ?, ?, ?, ?)";
        Database.update(sql,
                product.getProductName(),
                product.getDescription(),
                product.getCategoryId(),
                product.getBrandId(),
                product.getSeason(),
                product.getGender());
    }

    public void update(Product product) {
        String sql = "UPDATE Products SET product_name = ?, description = ?, category_id = ?, brand_id = ?, season = ?, gender = ? WHERE product_id = ?";
        Database.update(sql,
                product.getProductName(),
                product.getDescription(),
                product.getCategoryId(),
                product.getBrandId(),
                product.getSeason(),
                product.getGender(),
                product.getProductId());
    }


    public void delete(int productId) {
        String sql = "DELETE FROM Products WHERE product_id = ?";
        Database.update(sql, productId);
    }
    

    public List<Object[]> searchByName(String name) {
        String sql = """
            SELECT
                p.product_id,
                p.product_name,
                c.category_name,
                b.brand_name,
                p.season,
                p.gender,
                p.description
            FROM
                Products p
            LEFT JOIN
                Categories c ON p.category_id = c.category_id
            LEFT JOIN
                Brands b ON p.brand_id = b.brand_id
            WHERE
                p.product_name LIKE ?
        """;
        List<Object[]> list = new ArrayList<>();
        try {
            ResultSet rs = Database.query(sql, "%" + name + "%");
            while (rs.next()) {
                 list.add(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    rs.getString("brand_name"),
                    rs.getString("season"),
                    rs.getString("gender"),
                    rs.getString("description")
                });
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm kiếm sản phẩm", e);
        }
        return list;
    }
    
    public Product selectById(int productId) {
    String sql = "SELECT * FROM Products WHERE product_id = ?";
    List<Product> list = select(sql, productId); // Giả sử bạn đã có hàm select chung
    return list.isEmpty() ? null : list.get(0);
}
    public int insertAndGetId(Product product) {
    // Câu lệnh này dành riêng cho SQL Server
    String sql = "INSERT INTO Products (product_name, description, category_id, brand_id, season, gender) VALUES (?, ?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();";
    Object generatedId = Database.value(sql,
            product.getProductName(),
            product.getDescription(),
            product.getCategoryId(),
            product.getBrandId(),
            product.getSeason(),
            product.getGender());

    if (generatedId instanceof BigDecimal) {
        return ((BigDecimal) generatedId).intValue();
    } else if (generatedId instanceof Number) {
        return ((Number) generatedId).intValue();
    }
    throw new RuntimeException("Không thể lấy ID của sản phẩm vừa tạo.");
}
}