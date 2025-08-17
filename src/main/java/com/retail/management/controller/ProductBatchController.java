package com.retail.management.controller;

import com.retail.management.dto.ProductBatchDTO;
import com.retail.management.dto.ProductDTO;
import com.retail.management.dto.SimpleBatchProduct;
import com.retail.management.entity.Product;
import com.retail.management.entity.ProductBatch;
import com.retail.management.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productBatch")
@CrossOrigin
public class ProductBatchController {
    private final ProductService productService;

    @Operation(summary = "Create a new product Batch")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
//    @PostMapping
//    public ResponseEntity<ProductBatch> create(@RequestBody ProductBatch dto) {
//        ProductBatch saved = productService.createBatch(dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
//    }
    @PostMapping
    public ResponseEntity<ProductBatch> create(@RequestBody SimpleBatchProduct dto) {
        ProductBatch saved = productService.createSimpleBatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Get product by id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductBatchDTO> get(@PathVariable Long id) {
        return productService.getBatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update product")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Product updated = productService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean flag = productService.deleteBatchProduct(id);
        return flag?ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "List all products")
    @GetMapping
    public ResponseEntity<List<ProductBatchDTO>> listAll() {
        return ResponseEntity.ok(productService.listAllBatch());
    }

    @Operation(summary = "Get low-stock products")
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> lowStock(@RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(productService.lowStock(threshold));
    }
}
