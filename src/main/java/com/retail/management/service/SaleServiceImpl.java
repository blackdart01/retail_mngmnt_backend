package com.retail.management.service;

import com.retail.management.dto.SimpleSale;
import com.retail.management.entity.*;
import com.retail.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final SaleItemRepository saleItemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ProductBatchRepository productBatchRepository;

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public Sale recordSale(SimpleSale simpleSale) {
        User user = userRepository.findByUsername(simpleSale.getUser().getUsername()).orElse(null);
        List<SaleItem> saleItemList = new ArrayList<>();
        AtomicReference<Double> totalPrice = new AtomicReference<>(0.0);
        List<ProductBatch> productBatchList = new ArrayList<>();
        Sale sale = new Sale();
        sale.setSaleDate(Instant.now());
        sale.setInvoiceNumber(generateInvoiceNumber());
        sale.setUser(user);
        for(SimpleSale.SaleItem saleItem : simpleSale.getItems()){
//            ProductBatch productBatch = productBatchRepository.findById(saleItem.getProductId()).orElse(null);
            ProductBatch productBatch = productBatchRepository.findWithProduct(saleItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product Not Found"));
            if(ObjectUtils.isEmpty(productBatch)){
                throw new RuntimeException("Product Not Found");
            }
            if(productBatch.getQuantity() < saleItem.getQuantity()){
                throw new RuntimeException("Not enough stock for product");
            }
            double price = 0.0;
            double sellingPrice = 0.0;
            if(ObjectUtils.isNotEmpty(saleItem.getPrice())){
                sellingPrice = saleItem.getPrice();
                price = saleItem.getPrice()*saleItem.getQuantity();
//                totalPrice.set(totalPrice.get() + (saleItem.getPrice())*saleItem.getQuantity());
            } else {
                sellingPrice = productBatch.getSellingPrice();
                price = productBatch.getSellingPrice() * saleItem.getQuantity();
//                totalPrice.set(totalPrice.get() + (productBatch.getSellingPrice() * saleItem.getQuantity()));
            }
            totalPrice.set(totalPrice.get() + price);
            Product product = productBatch.getProduct(); // use directly from the batch
            if (product == null) {
                throw new RuntimeException("Batch has no linked product");
            }

            saleItemList.add(SaleItem.builder()
                    .product(product)
                    .quantity(saleItem.getQuantity())
                    .sale(sale)
                    .unitPrice(sellingPrice)
                    .totalProductCost(price)
                    .build());
            productBatch.setBackstoreQuantity(productBatch.getBackstoreQuantity()-saleItem.getQuantity());
            productBatch.setQuantity(productBatch.getBackstoreQuantity()+productBatch.getRackQuantity());
            product.setBackstoreQuantity(product.getBackstoreQuantity() - saleItem.getQuantity());
            product.setStockQuantity(product.getBackstoreQuantity() - product.getRackQuantity());
            productRepository.saveAndFlush(product);
            productBatchList.add(productBatch);
        }
        sale.setItems(saleItemList);
        sale.setTotalPrice(totalPrice.get());
        productBatchRepository.saveAllAndFlush(productBatchList);
        return saleRepository.saveAndFlush(sale);

//        for (SaleItem item : sale.getItems()) {
//            Product p = productRepository.findById(item.getProduct().getId())
//                    .orElseThrow(() -> new RuntimeException("Product not found"));
//
//            if (p.getStockQuantity() < item.getQuantity()) {
//                throw new RuntimeException("Not enough stock for product: " + p.getName());
//            }
//
//            // Deduct stock
//            p.setStockQuantity(p.getStockQuantity() - item.getQuantity());
//            productRepository.save(p);
//
//            // Set back-reference & price
//            item.setSale(sale);
////            item.setUnitPrice(p.getPrice());
//        }

//        return saleRepository.save(sale);
    }

    @Override
    public Optional<Sale> getById(Long id) {
        return saleRepository.findById(id);
    }

    @Override
    public List<Sale> listAll() {
        return saleRepository.findAll();
    }

    @Override
    public List<Sale> getSalesBetween(Instant from, Instant to) {
        return saleRepository.findBySaleDateBetween(from, to);
    }
}
