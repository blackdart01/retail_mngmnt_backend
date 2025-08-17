package com.retail.management.service;

import com.retail.management.dto.ProductBatchDTO;
import com.retail.management.dto.ProductDTO;
import com.retail.management.dto.SimpleBatchProduct;
import com.retail.management.dto.SimpleProduct;
import com.retail.management.entity.Product;
import com.retail.management.entity.ProductBatch;

import java.util.List;
import java.util.Optional;

public interface ProductService {
//    Product create(Product dto);
    Product createSimpleProduct(SimpleProduct simpleProduct);
    ProductBatch createSimpleBatch(SimpleBatchProduct simpleBatchProduct);
//    ProductBatch createBatch(ProductBatch dto);
    Product update(Long id, ProductDTO dto);
    Optional<ProductDTO> getById(Long id);
    Optional<ProductBatchDTO> getBatchById(Long id);
    void delete(Long id);
    Boolean deleteBatchProduct(Long id);
    List<ProductDTO> listAll();
    List<ProductBatchDTO> listAllBatch();
    List<ProductDTO> lowStock(int threshold);
    List<ProductDTO> lowStockProduct(boolean considerRackQuantityAlso);
    List<ProductBatchDTO> toBatchDTOWithoutProduct(List<ProductBatch> productBatches);
    ProductBatchDTO toSingleBatchDTO(ProductBatch product);
    Product getByBatchId(Long batchId);
}
