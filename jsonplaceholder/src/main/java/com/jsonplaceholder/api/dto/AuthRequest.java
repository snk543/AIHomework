package com.jsonplaceholder.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
} 