package com.retail.management.dto;

import lombok.Builder;
import lombok.Data;

@Data@Builder
public class SimpleProduct {
    private String name;
    private String sku;
    private String description;
    private Long categoryId;
    private Integer lowStockThreshold=0;
}
