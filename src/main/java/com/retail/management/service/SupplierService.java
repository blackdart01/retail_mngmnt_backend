package com.retail.management.service;

import com.retail.management.entity.Supplier;
import java.util.List;
import java.util.Optional;

public interface SupplierService {
    Supplier create(Supplier supplier);
    Supplier update(Long id, Supplier supplier);
    Optional<Supplier> getById(Long id);
    List<Supplier> listAll();
    void delete(Long id);
}
