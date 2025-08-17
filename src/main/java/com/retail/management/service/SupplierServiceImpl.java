package com.retail.management.service;

import com.retail.management.entity.Supplier;
import com.retail.management.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Supplier create(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier update(Long id, Supplier supplier) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        existing.setName(supplier.getName());
        existing.setContactPerson(supplier.getContactPerson());
        existing.setPhone(supplier.getPhone());
        existing.setEmail(supplier.getEmail());
        return supplierRepository.save(existing);
    }

    @Override
    public Optional<Supplier> getById(Long id) {
        return supplierRepository.findById(id);
    }

    @Override
    public List<Supplier> listAll() {
        return supplierRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        supplierRepository.deleteById(id);
    }
}