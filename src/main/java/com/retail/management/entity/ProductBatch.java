package com.retail.management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "product_batches")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;
//    private Long productId;

    @Column(nullable = false)
    private double costPrice;

    @Column(nullable = false)
    private double sellingPrice;

    @Column(nullable = false)
    private int quantity=0;
//    @NotNull
//    @Min(0)
    private Integer backstoreQuantity = 0;

//    @NotNull
//    @Min(0)
    private Integer rackQuantity = 0;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryLocation location; // BACKSTORE or RACK

    private LocalDate expiryDate;

    @Column(nullable = false, updatable = false)
    private Instant purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @JsonBackReference
    private Supplier supplier;

    private String referenceId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "purchase_order_id")
//    private PurchaseOrder purchaseOrder;

    @Column(length = 255)
    private String note;
}