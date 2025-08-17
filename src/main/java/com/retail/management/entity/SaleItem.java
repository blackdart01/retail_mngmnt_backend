package com.retail.management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sale_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SaleItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    @JsonBackReference
    private Sale sale;

//    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private ProductBatch productBatch;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @Min(1)
    private Integer quantity;

//    @NotNull
    private Double unitPrice;
    private Double totalProductCost;
}