package com.retail.management.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_movements", indexes = {
        @Index(name = "ix_inv_prod_date", columnList = "product_id, movement_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_batch_id")
    private ProductBatch productBatch; // <-- NEW: link to the batch

    // positive = quantity added to the destination; negative = removed
    @NotNull
    private Integer quantity;

    // source and destination helps reconstruct racks/backstore
    @Enumerated(EnumType.STRING)
    private InventoryLocation source; // BACKSTORE, RACK, EXTERNAL

    @Enumerated(EnumType.STRING)
    private InventoryLocation destination; // BACKSTORE, RACK, SOLD, EXTERNAL

    @NotBlank
    private String movementType; // PURCHASE, RESTOCK, ADJUSTMENT, BACKSTORE_TRANSFER, SALE_INFERRED

    @NotNull
    private Instant movementDate;

    // optional reference: purchase order id, user id, note
    private Long referenceId;
    private String note;
}