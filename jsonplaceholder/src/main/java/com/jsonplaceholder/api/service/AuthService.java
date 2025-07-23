package com.jsonplaceholder.api.service;

import com.jsonplaceholder.api.dto.AuthRequest;
import com.jsonplaceholder.api.dto.AuthResponse;
import com.jsonplaceholder.api.dto.LoginRequest;
import com.jsonplaceholder.api.dto.RegisterRequest;
import com.jsonplaceholder.api.dto.UserDto;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
} 