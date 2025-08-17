package com.retail.management.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password; // stored as hashed (BCrypt)

//    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;
}
