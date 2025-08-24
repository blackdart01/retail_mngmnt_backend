package com.retail.management.dto;

import com.retail.management.entity.WeightUnit;
import lombok.Builder;
import lombok.Data;

@Data@Builder
public class SimpleProduct {
    private String name;
    private WeightUnit weightUnits;
    private String weight;;
    private String description;
    private Long categoryId;
    private Integer lowStockThreshold=0;
}
