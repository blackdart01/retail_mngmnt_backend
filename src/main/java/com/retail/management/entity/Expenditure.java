package com.retail.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "expenditures")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expenditure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private Double amount;

    @NotNull
    private Instant date;

    private String category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank String description) {
        this.description = description;
    }

    public @NotNull Double getAmount() {
        return amount;
    }

    public void setAmount(@NotNull Double amount) {
        this.amount = amount;
    }

    public @NotNull Instant getDate() {
        return date;
    }

    public void setDate(@NotNull Instant date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
