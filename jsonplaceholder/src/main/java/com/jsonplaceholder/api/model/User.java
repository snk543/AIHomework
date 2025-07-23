package com.jsonplaceholder.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @Embedded
    private Address address;

    private String phone;
    private String website;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "company_name")),
        @AttributeOverride(name = "catchPhrase", column = @Column(name = "catch_phrase")),
        @AttributeOverride(name = "bs", column = @Column(name = "bs"))
    })
    private Company company;
} 