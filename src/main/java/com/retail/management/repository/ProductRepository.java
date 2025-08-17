package com.retail.management.repository;

import com.retail.management.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.backstoreQuantity <= :threshold ORDER BY p.backstoreQuantity ASC")
    List<Product> findLowStock(@Param("threshold") int threshold);

    List<Product> findByCategoryId(Long categoryId);

    // Pessimistic lock method — used to serialize updates to a product row
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
