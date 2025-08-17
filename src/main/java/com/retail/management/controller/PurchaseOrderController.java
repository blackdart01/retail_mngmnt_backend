package com.retail.management.controller;

import com.retail.management.entity.PurchaseOrder;
import com.retail.management.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService poService;

    public PurchaseOrderController(PurchaseOrderService poService) {
        this.poService = poService;
    }

    @Operation(summary = "Create a purchase order")
    @PostMapping
    public ResponseEntity<PurchaseOrder> create(@RequestBody PurchaseOrder po) {
        return ResponseEntity.ok(poService.create(po));
    }

    @Operation(summary = "Mark purchase order as received and update stock")
    @PutMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrder> markAsReceived(@PathVariable Long id) {
        return ResponseEntity.ok(poService.markAsReceived(id));
    }

    @Operation(summary = "Get purchase order by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getById(@PathVariable Long id) {
        return poService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "List all purchase orders")
    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> listAll() {
        return ResponseEntity.ok(poService.listAll());
    }

    @Operation(summary = "Get purchase orders between two dates")
    @GetMapping("/between")
    public ResponseEntity<List<PurchaseOrder>> getOrdersBetween(@RequestParam Instant from, @RequestParam Instant to) {
        return ResponseEntity.ok(poService.getOrdersBetween(from, to));
    }
}
