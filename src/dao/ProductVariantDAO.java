/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.VariantDetailDTO;
import java.util.ArrayList;
import java.util.List;
import model.ProductVariant;
import utils.Database;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductVariantDAO {
    
    private ProductVariant readFromResultSet(ResultSet rs) throws SQLException {
        return new ProductVariant(
            rs.getInt("variant_id"),
            rs.getString("sku_code"),
            rs.getInt("product_id"),
            rs.getString("color"),
            rs.getString("size"),
            rs.getBigDecimal("import_price"),
            rs.getBigDecimal("retail_price"),
            rs.getInt("stock_quantity"),
            rs.getString("image_url")
        );
    }

    
    public void insert(ProductVariant variant) {
        String sql = "INSERT INTO ProductVariants (sku_code, product_id, color, size, import_price, retail_price, stock_quantity, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Database.update(sql, variant.getSkuCode(), variant.getProductId(), variant.getColor(), variant.getSize(), variant.getImportPrice(), variant.getRetailPrice(), variant.getStockQuantity(), variant.getImageUrl());
    }

   
    public void update(ProductVariant variant) {
        String sql = "UPDATE ProductVariants SET sku_code=?, product_id=?, color=?, size=?, import_price=?, retail_price=?, stock_quantity=?, image_url=? WHERE variant_id=?";
        Database.update(sql, variant.getSkuCode(), variant.getProductId(), variant.getColor(), variant.getSize(), variant.getImportPrice(), variant.getRetailPrice(), variant.getStockQuantity(), variant.getImageUrl(), variant.getVariantId());
    }
    
    
    public void deleteBySkuCode(String skuCode) {
        String sql = "DELETE FROM ProductVariants WHERE sku_code = ?";
        Database.update(sql, skuCode);
    }

    
    public void updateStock(int variantId, int quantityChange) {
        String sql = "UPDATE ProductVariants SET stock_quantity = stock_quantity + ? WHERE variant_id = ?";
        Database.update(sql, quantityChange, variantId);
    }

    
    public List<ProductVariant> selectAll() {
        String sql = "SELECT * FROM ProductVariants";
        return select(sql);
    }

    
    public ProductVariant selectById(int variantId) {
        String sql = "SELECT * FROM ProductVariants WHERE variant_id = ?";
        List<ProductVariant> list = select(sql, variantId);
        return list.isEmpty() ? null : list.get(0);
    }

   
    public ProductVariant selectBySkuCode(String skuCode) {
        String sql = "SELECT * FROM ProductVariants WHERE sku_code = ?";
        List<ProductVariant> list = select(sql, skuCode);
        return list.isEmpty() ? null : list.get(0);
    }

    
    private List<ProductVariant> select(String sql, Object... args) {
        List<ProductVariant> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu ProductVariant", e);
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

  
    public List<VariantDetailDTO> selectAllVariantDetails() {
        String sql = """
            SELECT
                pv.variant_id, pv.sku_code, pv.color, pv.size, pv.retail_price, pv.stock_quantity,
                p.product_name,
                c.category_name,
                b.brand_name
            FROM ProductVariants pv
            LEFT JOIN Products p ON pv.product_id = p.product_id
            LEFT JOIN Categories c ON p.category_id = c.category_id
            LEFT JOIN Brands b ON p.brand_id = b.brand_id
            ORDER BY p.product_name, pv.color, pv.size
        """;

        List<VariantDetailDTO> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql);
            while (rs.next()) {
                VariantDetailDTO dto = new VariantDetailDTO();
                // Dữ liệu từ bảng ProductVariants
                dto.setVariantId(rs.getInt("variant_id"));
                dto.setSkuCode(rs.getString("sku_code"));
                dto.setColor(rs.getString("color"));
                dto.setSize(rs.getString("size"));
                dto.setRetailPrice(rs.getBigDecimal("retail_price"));
                dto.setStockQuantity(rs.getInt("stock_quantity"));
                // Dữ liệu từ các bảng JOIN
                dto.setProductName(rs.getString("product_name"));
                dto.setCategoryName(rs.getString("category_name"));
                dto.setBrandName(rs.getString("brand_name"));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn chi tiết sản phẩm", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    // Bỏ qua
                }
            }
        }
        return list;
    }
    public List<ProductVariant> selectByProductId(int productId) {
    String sql = "SELECT * FROM ProductVariants WHERE product_id = ?";
    return select(sql, productId); // Giả sử bạn đã có hàm select chung
}
    public void delete(int variantId) {
    String sql = "DELETE FROM ProductVariants WHERE variant_id = ?";
    Database.update(sql, variantId);
}
}
