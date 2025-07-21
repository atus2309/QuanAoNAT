/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

public class ProductVariant {
    private int variantId;
    private String skuCode;
    private int productId;
    private String color;
    private String size;
    private BigDecimal importPrice;
    private BigDecimal retailPrice;
    private int stockQuantity;
    private String imageUrl;

    public ProductVariant() {
    }
    
    public ProductVariant(int variantId, String skuCode, int productId, String color, String size, BigDecimal importPrice, BigDecimal retailPrice, int stockQuantity, String imageUrl) {
        this.variantId = variantId;
        this.skuCode = skuCode;
        this.productId = productId;
        this.color = color;
        this.size = size;
        this.importPrice = importPrice;
        this.retailPrice = retailPrice;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }
    
    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
