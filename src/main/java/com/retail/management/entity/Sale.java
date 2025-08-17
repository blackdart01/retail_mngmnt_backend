package com.retail.management.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "sales")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Sale {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Instant saleDate;

    @NotNull
    private Double totalPrice;

    @NotBlank
    @Column(unique = true)
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // One sale has many items
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
//    , orphanRemoval = true
    @JsonManagedReference
    private List<SaleItem> items = new ArrayList<>();
}