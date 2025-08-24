package com.retail.management.dto;

import com.retail.management.entity.InventoryLocation;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data@Builder
public class SimpleBatchProduct {
    private Long productId;
    private double costPrice;
    private double sellingPrice;
    private Integer backstoreQuantity;
    private Integer rackQuantity;
    private InventoryLocation location;
    private Instant expiryDate;
    private Instant purchaseDate;
    private Long supplierId;
    private String referenceId;
    private String note;
}
