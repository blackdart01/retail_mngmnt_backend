package com.retail.management.dto;

import com.retail.management.entity.InventoryLocation;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private InventoryLocation source;
    private InventoryLocation destination;
    private String movementType;
    private Instant movementDate;
    private Long referenceId;
    private String note;
}
