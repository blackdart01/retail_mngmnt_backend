package com.retail.management.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {@Index(name = "idx_sku", columnList = "sku", unique = true)})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String sku;

    @NotBlank
    @Size(max = 100)
    private String weight;

    @NotBlank
    @Size(max = 100)
    private String weightUnits;

    @Column(columnDefinition = "text")
    private String description;

//    @NotNull
//    @DecimalMin("0.0")
//    private Double price; // selling price
//
//    @NotNull
//    @DecimalMin("0.0")
//    private Double costPrice; // purchase/cost price per unit
//
//    // we keep stockQuantity for backward compatibility (optional)
//    @Deprecated
    private Integer stockQuantity;

    private String referenceId;
//    private LocalDate expiryDate;

//    @NotNull
//    @Min(0)
    private Integer backstoreQuantity = 0;

//    @NotNull
//    @Min(0)
    private Integer rackQuantity = 0;
    private Integer lowStockThreshold = 0;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

//    @ManyToOne
//    @JoinColumn(name = "supplier_id")
//    private Supplier supplier;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductBatch> batches = new ArrayList<>();
}