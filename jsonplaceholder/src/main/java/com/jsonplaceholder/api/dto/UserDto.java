package com.jsonplaceholder.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    private AddressDto address;
    private String phone;
    private String website;
    private CompanyDto company;
} 