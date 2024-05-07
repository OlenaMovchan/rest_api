package com.example.rest_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email pattern")
    private String email;

    @NotNull(message = "Field firstName is required")
    @NotBlank(message = "Field firstName is required")
    private String firstName;

    @NotNull(message = "Field lastName is required")
    @NotBlank(message = "Field lastName is required")
    private String lastName;

    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Field birthDate is required")
    private LocalDate birthDate;

    private String address;

    private String phoneNumber;

}
