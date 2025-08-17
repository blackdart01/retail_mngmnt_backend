package com.retail.management.dto;

import com.retail.management.entity.Category;
import com.retail.management.entity.ProductBatch;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private Category category;
//    private Long supplierId;
//    private Double price;
//    private Double costPrice;
    private Integer backstoreQuantity=0;
    private Integer rackQuantity = 0;
    private Integer stockQuantity=0;
    private String referenceId;
    private Integer lowStockThreshold=0;
    private List<ProductBatchDTO> batches;
}