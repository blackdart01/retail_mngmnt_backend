package com.retail.management.service;

import com.retail.management.dto.*;
import com.retail.management.entity.*;
import com.retail.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

//    @Override
//    public Product create(ProductDTO dto) {
//        Category cat = null;
//        if (dto.getCategory() != null) cat = categoryRepository.findById(dto.getCategory().getId()).orElse(null);
//
//        Product p = Product.builder()
//                .name(dto.getName())
//                .sku(dto.getSku())
//                .description(dto.getDescription())
//                .stockQuantity(dto.getBackstoreQuantity()+dto.getRackQuantity())
//                .backstoreQuantity(dto.getBackstoreQuantity())
//                .rackQuantity(dto.getRackQuantity())
//                .category(cat)
//                .build();
//        return productRepository.save(p);
//    }
//@Override
//public Product create(Product dto) {
//    Category cat = null;
//    if (dto.getCategory() != null) cat = categoryRepository.findById(dto.getCategory().getId()).orElse(null);
//
//    Product p = Product.builder()
//            .name(dto.getName())
//            .sku(dto.getSku())
//            .description(dto.getDescription())
//            .stockQuantity(dto.getBackstoreQuantity()+dto.getRackQuantity())
//            .backstoreQuantity(dto.getBackstoreQuantity())
//            .rackQuantity(dto.getRackQuantity())
//            .category(cat)
//            .build();
//    return productRepository.save(p);
//}

//    @Override
//    public ProductBatch createBatch(ProductBatchDTO dto){
//        Supplier sup = null;
//        if (dto.getSupplierName() != null) sup = supplierRepository.findById(dto.getSupplierName().getId()).orElse(null);
//        ProductBatch productBatch = ProductBatch.builder()
//                .product(productRepository.findById(dto.getProductId()).orElse(null))
//                .costPrice(dto.getCostPrice())
//                .sellingPrice(dto.getSellingPrice())
//                .expiryDate(dto.getExpiryDate())
//                .location(dto.getLocation())
//                .note(dto.getNote())
//                .purchaseDate(dto.getPurchaseDate())
//                .receivedAt(dto.getReceivedAt())
//                .quantity(dto.getBackstoreQuantity() + dto.getRackQuantity())
//                .backstoreQuantity(dto.getBackstoreQuantity())
//                .rackQuantity(dto.getRackQuantity())
//                .build();
//        return productBatchRepository.save(productBatch);
//    }

    public Product createSimpleProduct(SimpleProduct simpleProduct){
        Product product = Product.builder()
                .name(simpleProduct.getName())
                .category(categoryRepository.findById(simpleProduct.getCategoryId()).orElse(null))
                .description(simpleProduct.getDescription())
                .sku(simpleProduct.getSku())
                .backstoreQuantity(0)
                .rackQuantity(0)
                .stockQuantity(0)
                .referenceId("")
                .build();
        return productRepository.saveAndFlush(product);
    }
    @Transactional
    public ProductBatch createSimpleBatch(SimpleBatchProduct simpleBatchProduct){
        Supplier sup = null;
        Product prod = null;
        if (simpleBatchProduct.getSupplierId() != null) sup = supplierRepository.findById(simpleBatchProduct.getSupplierId()).orElse(null);
        if (simpleBatchProduct.getProductId() != null) prod = productRepository.findById(simpleBatchProduct.getProductId()).orElse(null);
        Product product = productRepository.findById(simpleBatchProduct.getProductId()).orElse(null);
        ProductBatch productBatch = null;
        if (ObjectUtils.isNotEmpty(product)) {
            productBatch = ProductBatch.builder()
                    .product(product)
                    .costPrice(simpleBatchProduct.getCostPrice())
                    .sellingPrice(simpleBatchProduct.getSellingPrice())
                    .expiryDate(simpleBatchProduct.getExpiryDate())
                    .location(simpleBatchProduct.getLocation())
                    .note(simpleBatchProduct.getNote())
                    .purchaseDate(simpleBatchProduct.getPurchaseDate())
                    .quantity(simpleBatchProduct.getBackstoreQuantity() + simpleBatchProduct.getRackQuantity())
                    .backstoreQuantity(simpleBatchProduct.getBackstoreQuantity())
                    .rackQuantity(simpleBatchProduct.getRackQuantity())
                    .supplier(supplierRepository.findById(simpleBatchProduct.getSupplierId()).orElse(null))
                    .build();
            productBatch = productBatchRepository.save(productBatch);
            product.setBackstoreQuantity((ObjectUtils.isNotEmpty(product.getBackstoreQuantity())?product.getBackstoreQuantity():0) + simpleBatchProduct.getBackstoreQuantity());
            product.setRackQuantity((ObjectUtils.isNotEmpty(product.getRackQuantity())?product.getRackQuantity():0) + simpleBatchProduct.getRackQuantity());
            product.setStockQuantity((ObjectUtils.isNotEmpty(product.getStockQuantity())?product.getStockQuantity():0) + productBatch.getQuantity());
            productRepository.saveAndFlush(product);
        }
        return productBatch;
    }
//@Override
//public ProductBatch createBatch(ProductBatch dto){
//    Supplier sup = null;
//    if (dto.getSupplier() != null) sup = supplierRepository.findById(dto.getSupplier().getId()).orElse(null);
//    ProductBatch productBatch = ProductBatch.builder()
//            .product(productRepository.findById(dto.getProductId()).orElse(null))
//            .costPrice(dto.getCostPrice())
//            .sellingPrice(dto.getSellingPrice())
//            .expiryDate(dto.getExpiryDate())
//            .location(dto.getLocation())
//            .note(dto.getNote())
//            .purchaseDate(dto.getPurchaseDate())
//            .receivedAt(dto.getReceivedAt())
//            .quantity(dto.getBackstoreQuantity() + dto.getRackQuantity())
//            .backstoreQuantity(dto.getBackstoreQuantity())
//            .rackQuantity(dto.getRackQuantity())
//            .build();
//    return productBatchRepository.save(productBatch);
//}
    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .lowStockThreshold(product.getLowStockThreshold())
                .description(product.getDescription())
                .stockQuantity(product.getStockQuantity())
                .referenceId(product.getReferenceId())
                .category(product.getCategory())
                .batches(toBatchDTOWithoutProduct(product.getBatches()))
                .backstoreQuantity(product.getBackstoreQuantity())
                .rackQuantity(product.getRackQuantity())
                .build();
    }
    public List<ProductBatchDTO> toBatchDTO(List<ProductBatch> productBatches) {
        List<ProductBatchDTO> productBatchDTOS = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(productBatches)) {
            for (ProductBatch product : productBatches) {
                productBatchDTOS.add(ProductBatchDTO.builder()
                        .id(product.getId())
                        .purchaseDate(product.getPurchaseDate())
                        .productId(product.getProduct().getId())
                        .product(toProductDTO(product.getProduct()))
                        .costPrice(product.getCostPrice())
                        .sellingPrice(product.getSellingPrice())
                        .referenceId(product.getReferenceId())
                        .location(product.getLocation())
                        .supplierName(toSplDTO(product.getSupplier()))
                        .note(product.getNote())
                        .quantity(product.getBackstoreQuantity() + product.getRackQuantity())
                        .backstoreQuantity(product.getBackstoreQuantity())
                        .rackQuantity(product.getRackQuantity())
                        .build());
            }
        }
        return productBatchDTOS;
    }
    public List<ProductBatchDTO> toBatchDTOWithoutProduct(List<ProductBatch> productBatches) {
        List<ProductBatchDTO> productBatchDTOS = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(productBatches)) {
            for (ProductBatch product : productBatches) {
                productBatchDTOS.add(ProductBatchDTO.builder()
                        .id(product.getId())
                        .purchaseDate(product.getPurchaseDate())
                        .productId(product.getProduct().getId())
                        .costPrice(product.getCostPrice())
                        .sellingPrice(product.getSellingPrice())
                        .referenceId(product.getReferenceId())
                        .location(product.getLocation())
                        .supplierName(toSplDTO(product.getSupplier()))
                        .note(product.getNote())
                        .quantity(product.getBackstoreQuantity() + product.getRackQuantity())
                        .backstoreQuantity(product.getBackstoreQuantity())
                        .rackQuantity(product.getRackQuantity())
                        .build());
            }
        }
        return productBatchDTOS;
    }
    public ProductDTO toProductDTO(Product product){
        if(ObjectUtils.isEmpty(product))
            return null;
        AtomicInteger totalBackstoreQuantity = new AtomicInteger(0);
        AtomicInteger totalQuantity = new AtomicInteger(0);
        AtomicInteger totalRackQuantity = new AtomicInteger(0);
        List<ProductBatchDTO> productBatchDTOS = toBatchDTOWithoutProduct(product.getBatches());
        for(ProductBatch productBatches : product.getBatches()){
            totalBackstoreQuantity.set(totalBackstoreQuantity.get() + productBatches.getBackstoreQuantity());
            totalQuantity.set(totalQuantity.get() + productBatches.getQuantity());
            totalRackQuantity.set(totalRackQuantity.get() + productBatches.getRackQuantity());
        }
        return ProductDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .rackQuantity(product.getRackQuantity())
                .backstoreQuantity(product.getBackstoreQuantity())
                .lowStockThreshold(product.getLowStockThreshold())
                .sku(product.getSku())
                .category(product.getCategory())
                .stockQuantity(product.getStockQuantity())
                .id(product.getId())
                .batches(productBatchDTOS)
                .referenceId(product.getReferenceId()).build();
    }
    public ProductBatchDTO toSingleBatchDTO(ProductBatch product) {
        if(ObjectUtils.isNotEmpty(product)) {
                return ProductBatchDTO.builder()
                        .id(product.getId())
                        .purchaseDate(product.getPurchaseDate())
                        .productId(product.getProduct().getId())
                        .product(toProductDTO(product.getProduct()))
                        .costPrice(product.getCostPrice())
                        .sellingPrice(product.getSellingPrice())
                        .referenceId(product.getReferenceId())
                        .location(product.getLocation())
                        .supplierName(toSplDTO(product.getSupplier()))
                        .note(product.getNote())
                        .quantity(product.getBackstoreQuantity() + product.getRackQuantity())
                        .backstoreQuantity(product.getBackstoreQuantity())
                        .rackQuantity(product.getRackQuantity())
                        .build();
            }
        return null;
    }

    public static SupplierDTO toSplDTO(Supplier supplier) {
        if(ObjectUtils.isNotEmpty(supplier)) {
                return SupplierDTO.builder()
                        .id(supplier.getId())
                        .phone(supplier.getPhone())
                        .email(supplier.getEmail())
                        .name(supplier.getName())
                        .contactPerson(supplier.getContactPerson())
                        .build();
            }
        return null;
    }

    @Override
    public Product update(Long id, ProductDTO dto) {
//        Product p = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
//        if (dto.getName() != null) p.setName(dto.getName());
//        if (dto.getSku() != null) p.setSku(dto.getSku());
//        if (dto.getDescription() != null) p.setDescription(dto.getDescription());
//        if (dto.getPrice() != null) p.setPrice(dto.getPrice());
//        if (dto.getStockQuantity() != null) p.setStockQuantity(dto.getStockQuantity());
//        if (dto.getCategoryId() != null) p.setCategory(categoryRepository.findById(dto.getCategoryId()).orElse(null));
//        if (dto.getSupplierId() != null) p.setSupplier(supplierRepository.findById(dto.getSupplierId()).orElse(null));
//        return productRepository.save(p);
        return null;
    }

//    @Override
//    public Optional<ProductBatchDTO> getById(Long id) {
//        return productBatchRepository.findById(id).map(ProductServiceImpl::toDTO);
//    }
    @Override
    public Optional<ProductDTO> getById(Long id) {
        return Optional.ofNullable(toDTO(productRepository.findById(id).orElse(null)));
    }
    @Override
    public Optional<ProductBatchDTO> getBatchById(Long id) {
        return Optional.ofNullable(toSingleBatchDTO(productBatchRepository.findById(id).orElse(null)));
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
    @Override
    public Boolean deleteBatchProduct(Long id) {
        ProductBatch productBatch = productBatchRepository.findById(id).orElse(null);
        if(ObjectUtils.isNotEmpty(productBatch)){
            Product product = productRepository.findById(productBatch.getProduct().getId()).orElse(null);
            if (ObjectUtils.isNotEmpty(product)){
                if(ObjectUtils.isNotEmpty(product.getBackstoreQuantity())&&product.getBackstoreQuantity()>=productBatch.getBackstoreQuantity() && ObjectUtils.isNotEmpty(product.getRackQuantity())&&product.getRackQuantity()>=productBatch.getRackQuantity()){
                    product.setBackstoreQuantity((ObjectUtils.isNotEmpty(product.getBackstoreQuantity())?product.getBackstoreQuantity():0) - productBatch.getBackstoreQuantity());
                    product.setStockQuantity((ObjectUtils.isNotEmpty(product.getStockQuantity())?product.getStockQuantity():0) - productBatch.getQuantity());
                    product.setRackQuantity((ObjectUtils.isNotEmpty(product.getRackQuantity())?product.getRackQuantity():0) - productBatch.getRackQuantity());
                    productBatchRepository.deleteById(id);
                    productRepository.saveAndFlush(product);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ProductDTO> listAll() {
        List<Product> productList = productRepository.findAll();
        List<ProductDTO> productDTOS = new ArrayList<>();
        for(Product product : productList) {
            productDTOS.add(toProductDTO(product));
        }
        return productDTOS;
    }

    public Product getByBatchId(Long batchId){
        ProductBatch productBatch = productBatchRepository.findById(batchId).orElse(null);
        Product product = productRepository.findById(productBatch.getId()).orElse(null);
        if(ObjectUtils.isNotEmpty(product)) {
            product.setBatches(List.of(productBatch));
        }
        return product;
    }
    @Override
    public List<ProductBatchDTO> listAllBatch() {
        List<ProductBatch> productBatches = productBatchRepository.findAll();
        return toBatchDTO(productBatches);
    }

    @Override
    public List<ProductDTO> lowStock(int threshold) {
        List<ProductDTO> productDTOS = listAll();
        return productDTOS.stream().filter(product -> product.getBackstoreQuantity()<threshold).toList();
//        return productRepository.findLowStock(threshold);
    }
    @Override
    public List<ProductDTO> lowStockProduct(boolean considerRackQuantityAlso) {
        List<ProductDTO> productDTOS = listAll();
        return productDTOS.stream().filter(product -> considerRackQuantityAlso?product.getStockQuantity()<=product.getLowStockThreshold() : product.getBackstoreQuantity()<=product.getLowStockThreshold()).toList();
//        return productRepository.findLowStock(threshold);
    }
}
