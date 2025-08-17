package com.retail.management.repository;

import com.retail.management.entity.Sale;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleDateBetween(Instant from, Instant to);

    boolean existsByInvoiceNumber(String invoiceNumber);
}