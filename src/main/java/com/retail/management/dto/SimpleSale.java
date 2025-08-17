package com.retail.management.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data@Builder
public class SimpleSale {
    private String invoiceNumber;
    private User user;
    private List<SaleItem> items;

    @Builder
    @Data
    public static class SaleItem{
        private Long productId;
        private Integer quantity;
        private Double price;
    }

    @Builder
    @Data
    public static class User{
        private String username;
    }
}
