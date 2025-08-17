package com.retail.management.repository;

import com.retail.management.entity.InventoryMovement;
import com.retail.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByProductAndMovementDateBetween(Product product, Instant from, Instant to);
    List<InventoryMovement> findByMovementDateBetween(Instant from, Instant to);
    Optional<InventoryMovement> findTopByProductOrderByMovementDateDesc(Product product);

}
