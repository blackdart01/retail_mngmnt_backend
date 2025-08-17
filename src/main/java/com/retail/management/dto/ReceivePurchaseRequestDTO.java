package com.retail.management.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivePurchaseRequestDTO {
    @NotNull
    private Long productId;

    @Min(1)
    private int quantity;

    @DecimalMin(value = "0.0", inclusive = false)
    private double unitCost;
    @DecimalMin(value = "0.0", inclusive = false)
    private double sellingCost;

    // optional purchase order id if available
    private Long purchaseOrderId;

    private String note;
    private Long supplierId;
}
