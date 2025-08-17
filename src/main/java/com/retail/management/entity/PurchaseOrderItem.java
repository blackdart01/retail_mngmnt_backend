package com.retail.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "purchase_order_items")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private Double unitPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public @NotNull @Min(1) Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull @Min(1) Integer quantity) {
        this.quantity = quantity;
    }

    public @NotNull Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(@NotNull Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
