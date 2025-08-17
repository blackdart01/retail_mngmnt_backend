package com.retail.management.controller;
import com.retail.management.dto.*;
import com.retail.management.entity.InventoryMovement;
import com.retail.management.entity.RangeType;
import com.retail.management.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Validated
@CrossOrigin
public class InventoryController {

    private final InventoryService inventoryService;
    // ModelMapper or manual mapping; add ModelMapper as bean if you like
    private final ModelMapper modelMapper = new ModelMapper();

//    @Operation(summary = "Receive purchase into backstore (adds to backstore and logs movement)")
//    @PostMapping("/receive")
//    public ResponseEntity<MovementResponseDTO> receivePurchase(@Valid @RequestBody ReceivePurchaseRequestDTO req) {
//        InventoryMovement mv = inventoryService.receivePurchase(req.getProductId(), req.getQuantity(), req.getUnitCost(),
//                req.getSellingCost(), null, req.getSupplierId(), req.getPurchaseOrderId(), req.getNote());
//        return ResponseEntity.ok(toDto(mv));
//    }

    @Operation(summary = "Move items from backstore to rack (restock to rack)")
    @PostMapping("/restock")
    public ResponseEntity<MovementResponseDTO> restock(@Valid @RequestBody RestockRequestDTO req) {
        InventoryMovement mv = inventoryService.restockToRack(req.getBatchId(), req.getQuantity(), req.getUserId(), req.getNote());
        return ResponseEntity.ok(toDto(mv));
    }

    @Operation(summary = "Manual adjustment of inventory (positive add, negative remove)")
    @PostMapping("/adjust")
    public ResponseEntity<MovementResponseDTO> adjust(@Valid @RequestBody AdjustRequestDTO req) {
        InventoryMovement mv = inventoryService.manualAdjust(req.getProductId(), req.getDeltaQuantity(), req.getNote());
        return ResponseEntity.ok(toDto(mv));
    }

    @Operation(summary = "Take daily snapshot (store current backstore/rack snapshot for the date)")
    @PostMapping("/snapshot")
    public ResponseEntity<Void> snapshot(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        inventoryService.takeDailySnapshot(date);
        return ResponseEntity.ok().build();
    }

    /*@Operation(summary = "Generate daily inferred-sales & expenditure report")
    @GetMapping("/report/daily")
    public ResponseEntity<DailyReportDTO> dailyReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyReportDTO dto = inventoryService.generateDailyReportDto(date);
        return ResponseEntity.ok(dto);
    }*/
    @Operation(summary = "Generate daily inferred-sales & expenditure report")
    @GetMapping("/report/daily")
    public ResponseEntity<DailyReportDTO> dailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyReportDTO dto = inventoryService.generateDailyReportDto(date);
        return ResponseEntity.ok(dto);
    }
    @Operation(summary = "Generate inferred-sales & expenditure report for any date range or predefined range type")
    @GetMapping("/sales/report/range")
    public ResponseEntity<DailyReportDTO> salesRangeReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) RangeType rangeType,
            @RequestParam(required = false) Integer daysCount // only for PAST_DAYS
    ) {
        LocalDate today = LocalDate.now();

        if (rangeType != null) {
            switch (rangeType) {
                case PAST_DAYS:
                    if (daysCount == null || daysCount <= 0) {
                        throw new IllegalArgumentException("daysCount must be > 0 for PAST_DAYS");
                    }
                    startDate = today.minusDays(daysCount);
                    endDate = today;
                    break;
                case PAST_WEEK:
                    startDate = today.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
                    endDate = startDate.plusDays(6);
                    break;
                case CURRENT_WEEK:
                    startDate = today.with(java.time.DayOfWeek.MONDAY);
                    endDate = today;
                    break;
                case PAST_MONTH:
                    startDate = today.minusMonths(1).withDayOfMonth(1);
                    endDate = startDate.plusMonths(1).minusDays(1);
                    break;
                case CURRENT_MONTH:
                    startDate = today.withDayOfMonth(1);
                    endDate = today;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported rangeType");
            }
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Either rangeType or both startDate and endDate must be provided");
        }

        DailyReportDTO dto = inventoryService.generateReportForRangeInSale(startDate, endDate);
        return ResponseEntity.ok(dto);
    }

    /*@Operation(summary = "Generate inferred-sales & expenditure report for any date range")
    @GetMapping("/report/range")
    public ResponseEntity<DailyReportDTO> rangeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        DailyReportDTO dto = inventoryService.generateReportForRange(startDate, endDate);
        return ResponseEntity.ok(dto);
    }*/
    @Operation(summary = "Generate inferred-sales & expenditure report for any date range or predefined range type")
    @GetMapping("/report/range")
    public ResponseEntity<DailyReportDTO> rangeReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) RangeType rangeType,
            @RequestParam(required = false) Integer daysCount // only for PAST_DAYS
    ) {
        LocalDate today = LocalDate.now();

        if (rangeType != null) {
            switch (rangeType) {
                case PAST_DAYS:
                    if (daysCount == null || daysCount <= 0) {
                        throw new IllegalArgumentException("daysCount must be > 0 for PAST_DAYS");
                    }
                    startDate = today.minusDays(daysCount);
                    endDate = today;
                    break;
                case PAST_WEEK:
                    startDate = today.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
                    endDate = startDate.plusDays(6);
                    break;
                case CURRENT_WEEK:
                    startDate = today.with(java.time.DayOfWeek.MONDAY);
                    endDate = today;
                    break;
                case PAST_MONTH:
                    startDate = today.minusMonths(1).withDayOfMonth(1);
                    endDate = startDate.plusMonths(1).minusDays(1);
                    break;
                case CURRENT_MONTH:
                    startDate = today.withDayOfMonth(1);
                    endDate = today;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported rangeType");
            }
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Either rangeType or both startDate and endDate must be provided");
        }

        DailyReportDTO dto = inventoryService.generateReportForRange(startDate, endDate);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get inventory movement logs between two instants")
    @GetMapping("/movements")
    public ResponseEntity<List<MovementResponseDTO>> movements(@RequestParam Instant from, @RequestParam Instant to) {
        List<InventoryMovement> list = inventoryService.getMovementsBetween(from, to);
        var res = list.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    // mapper helper
    private MovementResponseDTO toDto(InventoryMovement mv) {
        return MovementResponseDTO.builder()
                .id(mv.getId())
                .productId(mv.getProduct().getId())
                .productName(mv.getProduct().getName())
                .quantity(mv.getQuantity())
                .source(mv.getSource())
                .destination(mv.getDestination())
                .movementType(mv.getMovementType())
                .movementDate(mv.getMovementDate())
                .referenceId(mv.getReferenceId())
                .note(mv.getNote())
                .build();
    }
}
