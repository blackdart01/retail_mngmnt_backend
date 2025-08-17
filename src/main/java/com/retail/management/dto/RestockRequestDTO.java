package com.retail.management.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestockRequestDTO {
//    @NotNull
//    private Long productId;

    @Min(1)
    private int quantity;

    // optional: user who performed restock
    private Long userId;

    private String note;
    @NotNull
    private Long batchId;
}
