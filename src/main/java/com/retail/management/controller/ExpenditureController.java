package com.retail.management.controller;

import com.retail.management.entity.Expenditure;
import com.retail.management.service.ExpenditureService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @Operation(summary = "Create a new expenditure record")
    @PostMapping
    public ResponseEntity<Expenditure> create(@RequestBody Expenditure expenditure) {
        return ResponseEntity.ok(expenditureService.create(expenditure));
    }

    @Operation(summary = "Update an expenditure")
    @PutMapping("/{id}")
    public ResponseEntity<Expenditure> update(@PathVariable Long id, @RequestBody Expenditure expenditure) {
        return ResponseEntity.ok(expenditureService.update(id, expenditure));
    }

    @Operation(summary = "Get an expenditure by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Expenditure> getById(@PathVariable Long id) {
        return expenditureService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "List all expenditures")
    @GetMapping
    public ResponseEntity<List<Expenditure>> listAll() {
        return ResponseEntity.ok(expenditureService.listAll());
    }

    @Operation(summary = "Delete an expenditure")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenditureService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get expenditures between two dates")
    @GetMapping("/between")
    public ResponseEntity<List<Expenditure>> getExpendituresBetween(@RequestParam Instant from, @RequestParam Instant to) {
        return ResponseEntity.ok(expenditureService.getExpendituresBetween(from, to));
    }
}
