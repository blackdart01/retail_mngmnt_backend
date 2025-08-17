package com.retail.management.repository;

import com.retail.management.entity.InventorySnapshot;
import com.retail.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, Long> {
    Optional<InventorySnapshot> findByProductAndSnapshotDate(Product product, LocalDate date);
}
