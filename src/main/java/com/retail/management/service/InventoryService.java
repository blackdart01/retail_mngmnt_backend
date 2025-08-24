package com.retail.management.service;

import com.retail.management.dto.DailyReportDTO;
import com.retail.management.entity.InventoryMovement;
import java.time.*;
import java.util.List;

public interface InventoryService {
//    InventoryMovement receivePurchase(Long productId, int quantity, double unitCost, Long purchaseOrderId, String note);
    InventoryMovement restockToRack(Long batchId, int quantity, Long userId, String note);
    InventoryMovement manualAdjust(Long productId, int deltaQuantity, String note);
    void takeDailySnapshot(LocalDate date);
    DailyReportDTO generateDailyReportDto(LocalDate date);
    List<InventoryMovement> getMovementsBetween(Instant from, Instant to);
    DailyReportDTO generateReportForRange(LocalDate startDate, LocalDate endDate);
//    InventoryMovement receivePurchase(Long productId, int quantity, double unitCost, double sellingPrice, Long purchaseOrderId, String note, LocalDate expiryDate);
    InventoryMovement receivePurchase(Long productId, int quantity, double unitCost, double unitSellPrice, Instant expiryDate, Long supplierId, Long purchaseOrderId, String note);
    DailyReportDTO generateReportForRangeInSale(LocalDate startDate, LocalDate endDate);
}
