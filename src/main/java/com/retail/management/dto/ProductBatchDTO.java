package com.retail.management.dto;
import com.retail.management.entity.InventoryLocation;
import com.retail.management.entity.Product;
import com.retail.management.entity.Supplier;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class ProductBatchDTO {
    private Long id;
    private Long productId;
    private ProductDTO product;
    private double costPrice;
    private double sellingPrice;
    private int quantity;
    private InventoryLocation location;
    private LocalDate expiryDate;
    private Instant purchaseDate;
    private Instant receivedAt;
    private SupplierDTO supplierName;
    private Integer backstoreQuantity;
    private Integer rackQuantity;
    private String referenceId;
//    private Long purchaseOrderId;
    private String note;
}
