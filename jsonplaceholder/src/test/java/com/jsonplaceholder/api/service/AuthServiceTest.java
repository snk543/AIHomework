package com.jsonplaceholder.api.service;

import com.jsonplaceholder.api.dto.AuthRequest;
import com.jsonplaceholder.api.dto.AuthResponse;
import com.jsonplaceholder.api.dto.LoginRequest;
import com.jsonplaceholder.api.dto.RegisterRequest;
import com.jsonplaceholder.api.dto.UserDto;
import com.jsonplaceholder.api.model.AuthUser;
import com.jsonplaceholder.api.model.User;
import com.jsonplaceholder.api.repository.AuthUserRepository;
import com.jsonplaceholder.api.repository.UserRepository;
import com.jsonplaceholder.api.security.JwtTokenProvider;
import com.jsonplaceholder.api.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthUser authUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");
        registerRequest.setEmail("test@example.com");

        authUser = new AuthUser();
        authUser.setName("testuser");
        authUser.setEmail("test@example.com");
        authUser.setPasswordHash("encodedPassword");

        testToken = "test.jwt.token";
    }

    @Test
    void login_ShouldReturnAuthResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authUser);
        when(tokenProvider.generateToken(authUser)).thenReturn(testToken);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(testToken, response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authUser);
    }

    @Test
    void register_ShouldReturnAuthResponse() {
        when(authUserRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(tokenProvider.generateToken(any(AuthUser.class))).thenReturn(testToken);
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(authUser);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(testToken, response.getToken());
        verify(authUserRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(authUserRepository).save(any(AuthUser.class));
        verify(tokenProvider).generateToken(any(AuthUser.class));
    }

    @Test
    void register_WhenEmailExists_ShouldThrowException() {
        when(authUserRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(authUserRepository).existsByEmail(registerRequest.getEmail());
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void register_WhenUsernameExists_ShouldThrowException() {
        when(authUserRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(authUserRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }
} 