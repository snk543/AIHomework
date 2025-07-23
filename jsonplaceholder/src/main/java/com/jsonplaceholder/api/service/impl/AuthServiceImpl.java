package com.jsonplaceholder.api.service.impl;

import com.jsonplaceholder.api.dto.AuthRequest;
import com.jsonplaceholder.api.dto.AuthResponse;
import com.jsonplaceholder.api.dto.UserDto;
import com.jsonplaceholder.api.model.AuthUser;
import com.jsonplaceholder.api.repository.AuthUserRepository;
import com.jsonplaceholder.api.repository.UserRepository;
import com.jsonplaceholder.api.model.User;
import com.jsonplaceholder.api.security.JwtTokenProvider;
import com.jsonplaceholder.api.service.AuthService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jsonplaceholder.api.dto.LoginRequest;
import com.jsonplaceholder.api.dto.RegisterRequest;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authUser);
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException("User with email " + request.getEmail() + " already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EntityExistsException("User with username " + request.getUsername() + " already exists");
        }

        AuthUser authUser = new AuthUser();
        authUser.setEmail(request.getEmail());
        authUser.setName(request.getUsername());
        authUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        authUserRepository.save(authUser);

        User user = new User();
        user.setName(request.getUsername());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(authUser);
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }
} 