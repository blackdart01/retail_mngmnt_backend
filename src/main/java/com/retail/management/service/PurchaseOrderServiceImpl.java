package com.retail.management.service;

import com.retail.management.entity.*;
import com.retail.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository poRepository;
    private final ProductRepository productRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository poRepository, ProductRepository productRepository) {
        this.poRepository = poRepository;
        this.productRepository = productRepository;
    }

    @Override
    public PurchaseOrder create(PurchaseOrder po) {
        po.setOrderDate(Instant.now());
        return poRepository.save(po);
    }

    @Override
    public PurchaseOrder markAsReceived(Long id) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        if (!po.isReceived()) {
            for (PurchaseOrderItem item : po.getItems()) {
                Product p = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                productRepository.save(p);
            }
            po.setReceived(true);
        }

        return poRepository.save(po);
    }

    @Override
    public Optional<PurchaseOrder> getById(Long id) {
        return poRepository.findById(id);
    }

    @Override
    public List<PurchaseOrder> listAll() {
        return poRepository.findAll();
    }

    @Override
    public List<PurchaseOrder> getOrdersBetween(Instant from, Instant to) {
        return poRepository.findByOrderDateBetween(from, to);
    }
}
