package com.retail.management.controller;

import com.retail.management.dto.ProductBatchDTO;
import com.retail.management.dto.ProductDTO;
import com.retail.management.dto.SimpleProduct;
import com.retail.management.entity.Product;
import com.retail.management.entity.ProductBatch;
import com.retail.management.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody SimpleProduct dto) {
        Product saved = productService.createSimpleProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
// @Operation(summary = "Create a new product")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Created"),
//            @ApiResponse(responseCode = "400", description = "Invalid input")
//    })
//    @PostMapping
//    public ResponseEntity<Product> create(@RequestBody Product dto) {
//        Product saved = productService.create(dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
//    }

    @Operation(summary = "Get product by id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> get(@PathVariable Long id) {
        return productService.getById(id)
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
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all products")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> listAll() {
        return ResponseEntity.ok(productService.listAll());
    }

    @Operation(summary = "Get low-stock products")
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> lowStock(@RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(productService.lowStock(threshold));
    }
    @Operation(summary = "Get low-stock products")
    @GetMapping("/low-stock-products")
    public ResponseEntity<List<ProductDTO>> lowStockProduct(@RequestParam(defaultValue = "false") boolean considerRackQuantityAlso) {
        return ResponseEntity.ok(productService.lowStockProduct(considerRackQuantityAlso));
    }
}
