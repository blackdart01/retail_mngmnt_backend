package com.retail.management.service;

import com.retail.management.dto.SimpleSale;
import com.retail.management.entity.Sale;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SaleService {
    Sale recordSale(SimpleSale sale);
    Optional<Sale> getById(Long id);
    List<Sale> listAll();
    List<Sale> getSalesBetween(Instant from, Instant to);
}
