package com.retail.management.service;

import com.retail.management.dto.DailyReportDTO;
import com.retail.management.dto.ProductBatchDTO;
import com.retail.management.entity.*;
import com.retail.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventorySnapshotRepository snapshotRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductBatchRepository productBatchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductService productService;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;

    /**
     * Receive purchase into backstore.
     * Uses pessimistic lock on product to avoid concurrent updates.
     */
    /*@Override
    @Transactional
    public InventoryMovement receivePurchase(Long productId, int quantity, double unitCost, Long purchaseOrderId, String note) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // update cost price — you may implement weighted-average instead
        Double existingCost = product.getCostPrice() == null ? 0.0 : product.getCostPrice();
        if (existingCost == 0.0) product.setCostPrice(unitCost);
        else product.setCostPrice((existingCost + unitCost) / 2.0);

        product.setBackstoreQuantity(product.getBackstoreQuantity() + quantity);
        product.setStockQuantity(product.getBackstoreQuantity() + product.getRackQuantity());
        productRepository.save(product);

        if (purchaseOrderId != null) {
            purchaseOrderRepository.findById(purchaseOrderId).ifPresent(po -> {
                po.setReceived(true);
                purchaseOrderRepository.save(po);
            });
        }

        InventoryMovement mv = InventoryMovement.builder()
                .product(product)
                .quantity(quantity)
                .source(InventoryLocation.EXTERNAL)
                .destination(InventoryLocation.BACKSTORE)
                .movementType("PURCHASE_RECEIVE")
                .movementDate(Instant.now())
                .referenceId(purchaseOrderId)
                .note(note == null ? "Purchase received into backstore" : note)
                .build();
        return movementRepository.save(mv);
    }*/
    /**
     * Receive purchase into backstore as a separate batch.
     * No averaging of prices — each batch is tracked independently.
     */
    @Override
    @Transactional
    public InventoryMovement receivePurchase(Long productId, int quantity, double unitCost, double unitSellPrice,
                                             LocalDate expiryDate, Long supplierId, Long purchaseOrderId, String note) {

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        PurchaseOrder purchaseOrder = null;
        if (purchaseOrderId != null) {
            purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElse(null);
            if (purchaseOrder != null) {
                purchaseOrder.setReceived(true);
                purchaseOrderRepository.save(purchaseOrder);
            }
        }

        // Create new batch
        ProductBatch batch = ProductBatch.builder()
//                .product(product)
                .quantity(quantity)
                .costPrice(unitCost)
                .sellingPrice(unitSellPrice)
                .expiryDate(expiryDate)
//                .supplier(supplierRepository.findById(supplierId).orElse(null))
                .purchaseDate(Instant.now())
                .location(InventoryLocation.BACKSTORE)
//                .supplier(product.getSupplier())
                .referenceId(product.getReferenceId())
//                .purchaseOrder(purchaseOrder)
                .note(note == null ? "Purchase received into backstore" : note)
                .build();
        productBatchRepository.save(batch);

        // Update product aggregate quantities
        product.setBackstoreQuantity(product.getBackstoreQuantity() + quantity);
        product.setStockQuantity(product.getBackstoreQuantity() + product.getRackQuantity());
        productRepository.save(product);

        // Log movement
        InventoryMovement mv = InventoryMovement.builder()
                .product(product)
                .productBatch(batch)
                .quantity(quantity)
                .source(InventoryLocation.EXTERNAL)
                .destination(InventoryLocation.BACKSTORE)
                .movementType("PURCHASE_RECEIVE")
                .movementDate(Instant.now())
                .referenceId(purchaseOrderId)
                .note(note == null ? "Purchase received into backstore" : note)
                .build();

        return movementRepository.save(mv);
    }

    /**
     * Move items from backstore -> rack (restock), implying sold items per your business rule.
     * Uses pessimistic lock on product.
     */
    @Override
    @Transactional
    public InventoryMovement restockToRack(Long batchId, int quantity, Long userId, String note) {
//        Product product = productRepository.findByIdForUpdate(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        ProductBatch productBatch = productBatchRepository.findById(batchId).orElse(null);
        if ((ObjectUtils.isEmpty(productBatch.getBackstoreQuantity()) || productBatch.getBackstoreQuantity() < quantity)) {
            throw new RuntimeException("Insufficient backstore stock available:: for the batch:: " + productBatch.getBackstoreQuantity());
        }
        Product product = productRepository.findById(productBatch.getProduct().getId()).orElse(null);
        if (ObjectUtils.isEmpty(product)) {
            throw new RuntimeException("Product Not Found:: " + productBatch.getProduct().getId());
        }
        productBatch.setBackstoreQuantity(productBatch.getBackstoreQuantity() - quantity);
        productBatch.setRackQuantity(productBatch.getRackQuantity() + quantity);
        productBatchRepository.save(productBatch);

        product.setBackstoreQuantity(product.getBackstoreQuantity() - quantity);
        product.setRackQuantity(product.getRackQuantity() + quantity);
//        product.setStockQuantity(product.getBackstoreQuantity() + product.getRackQuantity());
        productRepository.save(product);

        InventoryMovement mv = InventoryMovement.builder()
                .product(product)
                .quantity(quantity)
                .source(InventoryLocation.BACKSTORE)
                .destination(InventoryLocation.RACK)
                .movementType("RESTOCK_TO_RACK")
                .movementDate(Instant.now())
                .referenceId(userId)
                .note(note == null ? "Restocked to rack" : note)
                .build();
        return movementRepository.save(mv);
    }

    /**
     * Manual adjustments to inventory. Positive -> add to backstore.
     * Negative -> remove from backstore first, then rack if required.
     */
    @Override
    @Transactional
    public InventoryMovement manualAdjust(Long productId, int deltaQuantity, String note) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        InventoryMovement primaryMovement;
        if (deltaQuantity > 0) {
            product.setBackstoreQuantity(product.getBackstoreQuantity() + deltaQuantity);
            primaryMovement = InventoryMovement.builder()
                    .product(product)
                    .quantity(deltaQuantity)
                    .source(InventoryLocation.EXTERNAL)
                    .destination(InventoryLocation.BACKSTORE)
                    .movementType("ADJUSTMENT_IN")
                    .movementDate(Instant.now())
                    .note(note)
                    .build();
            productRepository.save(product);
            movementRepository.save(primaryMovement);
            return primaryMovement;
        } else {
            int toRemove = -deltaQuantity;
            int removedFromBackstore = Math.min(product.getBackstoreQuantity(), toRemove);
            if (removedFromBackstore > 0) {
                product.setBackstoreQuantity(product.getBackstoreQuantity() - removedFromBackstore);
                primaryMovement = InventoryMovement.builder()
                        .product(product)
                        .quantity(-removedFromBackstore)
                        .source(InventoryLocation.BACKSTORE)
                        .destination(InventoryLocation.EXTERNAL)
                        .movementType("ADJUSTMENT_OUT")
                        .movementDate(Instant.now())
                        .note(note)
                        .build();
                movementRepository.save(primaryMovement);
                toRemove -= removedFromBackstore;
            } else {
                primaryMovement = null;
            }

            if (toRemove > 0) { // still need to remove => take from rack
                int removedFromRack = Math.min(product.getRackQuantity(), toRemove);
                if (removedFromRack > 0) {
                    product.setRackQuantity(product.getRackQuantity() - removedFromRack);
                    InventoryMovement mvRack = InventoryMovement.builder()
                            .product(product)
                            .quantity(-removedFromRack)
                            .source(InventoryLocation.RACK)
                            .destination(InventoryLocation.EXTERNAL)
                            .movementType("ADJUSTMENT_OUT")
                            .movementDate(Instant.now())
                            .note(note)
                            .build();
                    movementRepository.save(mvRack);
                    toRemove -= removedFromRack;
                }
            }

            productRepository.save(product);
            return primaryMovement != null ? primaryMovement : movementRepository.findTopByProductOrderByMovementDateDesc(product).orElse(null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovement> getMovementsBetween(Instant from, Instant to) {
        return movementRepository.findByMovementDateBetween(from, to);
    }

    @Override
    @Transactional
    public void takeDailySnapshot(LocalDate date) {
        List<Product> all = productRepository.findAll();
        for (Product p : all) {
            InventorySnapshot snap = InventorySnapshot.builder()
                    .product(p)
                    .snapshotDate(date)
                    .backstoreQuantity(p.getBackstoreQuantity())
                    .rackQuantity(p.getRackQuantity())
                    .build();

            snapshotRepository.findByProductAndSnapshotDate(p, date).ifPresentOrElse(existing -> {
                existing.setBackstoreQuantity(p.getBackstoreQuantity());
                existing.setRackQuantity(p.getRackQuantity());
                snapshotRepository.save(existing);
            }, () -> snapshotRepository.save(snap));
        }
    }

   /* *//**
     * Build a DailyReportDTO using RESTOCK_TO_RACK movements as "inferred sold".
     *//*
    @Override
    @Transactional(readOnly = true)
    public DailyReportDTO generateDailyReportDto(LocalDate date) {
        Instant dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<InventoryMovement> movements = movementRepository.findByMovementDateBetween(dayStart, dayEnd);

        Map<Long, Integer> movedToRack = new HashMap<>();
        for (InventoryMovement mv : movements) {
            if ("RESTOCK_TO_RACK".equals(mv.getMovementType())) {
                movedToRack.merge(mv.getProduct().getId(), mv.getQuantity(), Integer::sum);
            }
        }

        *//*Map<Long, Double> expenditurePerProduct = new HashMap<>();
        for (Long productId : movedToRack.keySet()) {
            productRepository.findById(productId).ifPresent(p -> {
                Double cost = p.getCostPrice() == null ? 0.0 : p.getCostPrice();
                expenditurePerProduct.put(productId, movedToRack.get(productId) * cost);
            });
        }
        double totalExpenditure = expenditurePerProduct.values().stream().mapToDouble(Double::doubleValue).sum();

        return DailyReportDTO.builder()
                .date(date)
                .inferredSalesCountPerProduct(movedToRack)
                .expenditurePerProduct(expenditurePerProduct)
                .salePerProduct(expenditurePerProduct)
                .totalExpenditure(totalExpenditure)
                .build();*//*

        Map<String, Map<String, Double>> priceBreakdownPerProduct = new HashMap<>();
        Map<String, Double> expenditurePerProduct = new HashMap<>();
        Map<String, Double> salesPerProduct = new HashMap<>();
        Map<String, Double> profitPerProduct = new HashMap<>();
        Map<String, Integer> inferredSalesCountPerProduct = new HashMap<>();

        for (Long productId : movedToRack.keySet()) {
            productRepository.findById(productId).ifPresent(p -> {
                String key = (p.getSku() != null && !p.getSku().isBlank())
                        ? p.getSku()
                        : p.getName();
                double sellPrice = p.getPrice() != null ? p.getPrice() : 0.0;
                double costPrice = p.getCostPrice() != null ? p.getCostPrice() : 0.0;
                int soldCount = movedToRack.get(productId);

                double totalCP = soldCount * costPrice;
                double totalSP = soldCount * sellPrice;
                double profit = totalSP - totalCP;

                priceBreakdownPerProduct.put(key, Map.of(
                        "CP", totalCP,
                        "SP", totalSP
                ));

                inferredSalesCountPerProduct .put(key, soldCount);
                expenditurePerProduct.put(key, totalCP);
                salesPerProduct.put(key, totalSP);
                profitPerProduct.put(key, profit);
            });
        }

        double totalExpenditure = expenditurePerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        double totalSale = salesPerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        double totalProfit = profitPerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        return DailyReportDTO.builder()
                .date(date)
                .inferredSalesCountPerProduct(inferredSalesCountPerProduct)
                .expenditurePerProduct(expenditurePerProduct) // CP totals
                .salesPerProduct(salesPerProduct)             // SP totals
                .profitPerProduct(profitPerProduct)           // SP - CP
                .totalExpenditure(totalExpenditure)
                .totalSale(totalSale)
                .totalProfit(totalProfit)
                .build();
    }*/
    @Override
    @Transactional(readOnly = true)
    public DailyReportDTO generateReportForRange(LocalDate startDate, LocalDate endDate) {
        Instant rangeStart = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant rangeEnd = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<InventoryMovement> movements = movementRepository.findByMovementDateBetween(rangeStart, rangeEnd);

        Map<Long, Integer> movedToRack = new HashMap<>();
        for (InventoryMovement mv : movements) {
            if ("RESTOCK_TO_RACK".equals(mv.getMovementType())) {
                movedToRack.merge(mv.getProduct().getId(), mv.getQuantity(), Integer::sum);
            }
        }

        Map<String, Map<String, Double>> priceBreakdownPerProduct = new HashMap<>();
        Map<String, Double> expenditurePerProduct = new HashMap<>();
        Map<String, Double> salesPerProduct = new HashMap<>();
        Map<String, Double> profitPerProduct = new HashMap<>();
        Map<String, Integer> inferredSalesCountPerProduct = new HashMap<>();
        Map<String, Integer> salesCountPerCategory = new HashMap<>();

        for (Long productId : movedToRack.keySet()) {
            productRepository.findById(productId).ifPresent(p -> {
                String key = (p.getSku() != null && !p.getSku().isBlank())
                        ? p.getSku()
                        : p.getName();
                List<ProductBatchDTO> productBatches = productService.toBatchDTOWithoutProduct(p.getBatches());
                AtomicReference<Double> totalCPBatch = new AtomicReference<>(0.0);
                AtomicReference<Double> totalSPBatch = new AtomicReference<>(0.0);
//                AtomicReference<Double> totalPBatch = new AtomicReference<>(0.0);
                for(ProductBatchDTO productBatchDTO : productBatches){
                    totalCPBatch.set(totalCPBatch.get() + productBatchDTO.getCostPrice()*productBatchDTO.getRackQuantity());
                    totalSPBatch.set(totalSPBatch.get() + productBatchDTO.getSellingPrice()*productBatchDTO.getRackQuantity());
//                    totalPBatch.set(totalSPBatch.get() - totalCPBatch.get());
                }
//                double sellPrice = p.getP() != null ? p.getPrice() : 0.0;
//                double costPrice = p.getCostPrice() != null ? p.getCostPrice() : 0.0;
//                double sellPrice = 0.0;
//                double costPrice = 0.0;
                int soldCount = movedToRack.get(productId);

//                double totalCP = soldCount * costPrice;
//                double totalSP = soldCount * sellPrice;
                double totalCP = totalCPBatch.get();
                double totalSP = totalSPBatch.get();
                double profit = totalSP - totalCP;

                priceBreakdownPerProduct.put(key, Map.of(
                        "CP", totalCP,
                        "SP", totalSP
                ));

                inferredSalesCountPerProduct.put(key, soldCount);
                expenditurePerProduct.put(key, totalCP);
                salesPerProduct.put(key, totalSP);
                profitPerProduct.put(key, profit);
                salesCountPerCategory.merge(p.getCategory().getName(), soldCount, Integer::sum);
            });
        }

        double totalExpenditure = expenditurePerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        double totalSale = salesPerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        double totalProfit = profitPerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        return DailyReportDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .inferredSalesCountPerProduct(inferredSalesCountPerProduct)
                .expenditurePerProduct(expenditurePerProduct) // CP totals
                .salesPerProduct(salesPerProduct)             // SP totals
                .profitPerProduct(profitPerProduct)           // SP - CP
                .salesCountPerCategory(salesCountPerCategory)
                .totalExpenditure(totalExpenditure)
                .totalSale(totalSale)
                .totalProfit(totalProfit)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DailyReportDTO generateReportForRangeInSale(LocalDate startDate, LocalDate endDate) {
        Instant rangeStart = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant rangeEnd = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<Sale> sales = saleRepository.findBySaleDateBetween(rangeStart, rangeEnd);

        Map<Long, Integer> movedToRack = new HashMap<>();
        for (Sale mv : sales) {
            for(SaleItem saleItem : mv.getItems()){
//                movedToRack.merge(saleItem.getProductBatch().getId(), saleItem.getQuantity(), Integer::sum);
                movedToRack.merge(saleItem.getProduct().getId(), saleItem.getQuantity(), Integer::sum);
            }
        }

        Map<String, Map<String, Double>> priceBreakdownPerProduct = new HashMap<>();
        Map<String, Double> expenditurePerProduct = new HashMap<>();
        Map<String, Double> salesPerProduct = new HashMap<>();
        Map<String, Double> profitPerProduct = new HashMap<>();
        Map<String, Integer> salesCountPerCategory = new HashMap<>();
        Map<String, Integer> inferredSalesCountPerProduct = new HashMap<>();
        for (Long productId : movedToRack.keySet()) {
            productRepository.findById(productId).ifPresent(p -> {
                String key = String.valueOf(p.getSku());
                ProductBatchDTO productBatchDTO = productService.toSingleBatchDTO(p.getBatches().get(0));
                AtomicReference<Double> totalCPBatch = new AtomicReference<>(0.0);
                AtomicReference<Double> totalSPBatch = new AtomicReference<>(0.0);
                totalCPBatch.set(productBatchDTO.getCostPrice()*movedToRack.get(productId));
                totalSPBatch.set(productBatchDTO.getSellingPrice()*movedToRack.get(productId));
                double totalCP = totalCPBatch.get();
                double totalSP = totalSPBatch.get();
                double profit = totalSP - totalCP;

                priceBreakdownPerProduct.put(key, Map.of(
                        "CP", totalCP,
                        "SP", totalSP
                ));

                inferredSalesCountPerProduct.put(key, movedToRack.get(productId));
                expenditurePerProduct.put(key, totalCP);
                salesPerProduct.put(key, totalSP);
                profitPerProduct.put(key, profit);
                salesCountPerCategory.merge(p.getCategory().getName(), movedToRack.get(productId), Integer::sum);
            });
        }

        double totalExpenditure = expenditurePerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        double totalSale = salesPerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        double totalProfit = profitPerProduct.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        return DailyReportDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .inferredSalesCountPerProduct(inferredSalesCountPerProduct)
                .expenditurePerProduct(expenditurePerProduct) // CP totals
                .salesPerProduct(salesPerProduct)             // SP totals
                .profitPerProduct(profitPerProduct)           // SP - CP
                .totalExpenditure(totalExpenditure)
                .salesCountPerCategory(salesCountPerCategory)
                .totalSale(totalSale)
                .totalProfit(totalProfit)
                .build();
    }

    // For daily usage
    @Override
    @Transactional(readOnly = true)
    public DailyReportDTO generateDailyReportDto(LocalDate date) {
        return generateReportForRange(date, date);
    }
}