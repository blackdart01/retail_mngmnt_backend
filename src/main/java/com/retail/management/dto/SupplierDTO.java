package com.retail.management.dto;
import com.retail.management.entity.InventoryLocation;
import com.retail.management.entity.Supplier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class SupplierDTO {
    private Long id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
}
