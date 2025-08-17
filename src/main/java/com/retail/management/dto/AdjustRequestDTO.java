package com.retail.management.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjustRequestDTO {
    @NotNull
    private Long productId;

    // negative -> remove, positive -> add
    @NotNull
    private Integer deltaQuantity;

    private String note;
}
