/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;


public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int variantId;
    private int quantity;
    private BigDecimal pricePerUnit; // Lưu lại giá tại thời điểm bán

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailId, int orderId, int variantId, int quantity, BigDecimal pricePerUnit) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    // Getters and Setters
    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
