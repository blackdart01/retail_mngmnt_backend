package com.retail.management.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventory_snapshots", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "snapshot_date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventorySnapshot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Product product;

    private LocalDate snapshotDate;

    private Integer backstoreQuantity;

    private Integer rackQuantity;
}
