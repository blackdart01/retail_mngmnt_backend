package com.retail.management.service;

import com.retail.management.entity.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class CronService {

    private final SupplierService supplierService;
    @Scheduled(fixedRate = 300000) // Executes every 5 minutes from the start of the previous execution
    public void updateSupplierData() {
        System.out.println(Instant.now() + " Scheduled task: Updating supplier data...");
        Supplier supplier = supplierService.getById(0L).orElse(null);
    }
}
