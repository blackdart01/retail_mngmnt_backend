package com.retail.management.service;


import com.retail.management.entity.PurchaseOrder;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {
    PurchaseOrder create(PurchaseOrder po);
    PurchaseOrder markAsReceived(Long id);
    Optional<PurchaseOrder> getById(Long id);
    List<PurchaseOrder> listAll();
    List<PurchaseOrder> getOrdersBetween(Instant from, Instant to);
}
