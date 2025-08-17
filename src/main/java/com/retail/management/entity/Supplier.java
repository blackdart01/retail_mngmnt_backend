package com.retail.management.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Supplier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 150)
    private String name;

    @Size(max = 150)
    private String contactPerson;

    @Size(max = 30)
    private String phone;

//    @Email
    private String email;
//    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<ProductBatch> batches = new ArrayList<>();
}
