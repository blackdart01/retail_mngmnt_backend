package com.retail.management.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyReportDTO {
    private LocalDate date;
    // productId -> inferred sold quantity
    private Map<String, Integer> inferredSalesCountPerProduct;
    // productId -> expenditure amount
    private Map<String, Double> expenditurePerProduct;
    private Map<String, Double> salesPerProduct;
    private Map<String, Double> profitPerProduct;
    private Map<String, Integer> salesCountPerCategory;
    private Double totalExpenditure;
    private Double totalSale;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalProfit;
}

