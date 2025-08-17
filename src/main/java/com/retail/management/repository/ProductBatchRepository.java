package com.retail.management.repository;

import com.retail.management.entity.InventoryLocation;
import com.retail.management.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {
    List<ProductBatch> findByProductIdAndLocationOrderByPurchaseDateAsc(Long productId, InventoryLocation location);
    @Query("SELECT pb FROM ProductBatch pb JOIN FETCH pb.product WHERE pb.id = :id")
    Optional<ProductBatch> findWithProduct(@Param("id") Long id);
}