package com.retail.management.controller;

import com.retail.management.dto.SimpleSale;
import com.retail.management.entity.Sale;
import com.retail.management.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @Operation(summary = "Record a new sale transaction")
    @PostMapping
    public ResponseEntity<Sale> recordSale(@RequestBody SimpleSale sale) {
        return ResponseEntity.ok(saleService.recordSale(sale));
    }

    @Operation(summary = "Get a sale by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getById(@PathVariable Long id) {
        return saleService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "List all sales")
    @GetMapping
    public ResponseEntity<List<Sale>> listAll() {
        return ResponseEntity.ok(saleService.listAll());
    }

    @Operation(summary = "Get sales between two dates")
    @GetMapping("/between")
    public ResponseEntity<List<Sale>> getSalesBetween(@RequestParam String from, @RequestParam String to) {
        LocalDate localDate = LocalDate.parse(from);
        LocalDate toInstantDate = LocalDate.parse(to);
        Instant fromInstant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant toInstant = toInstantDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        return ResponseEntity.ok(saleService.getSalesBetween(fromInstant, toInstant));
    }
}
