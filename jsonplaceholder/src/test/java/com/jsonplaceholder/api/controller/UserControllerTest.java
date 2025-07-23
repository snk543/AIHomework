package com.jsonplaceholder.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsonplaceholder.api.dto.UserDto;
import com.jsonplaceholder.api.security.JwtAuthenticationFilter;
import com.jsonplaceholder.api.security.JwtTokenProvider;
import com.jsonplaceholder.api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("Test User");
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setPhone("1234567890");
        testUserDto.setWebsite("www.test.com");
    }

    @Test
    @WithMockUser
    void getAllUsers_ShouldReturnUsers() throws Exception {
        List<UserDto> users = Arrays.asList(testUserDto);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(testUserDto.getName()))
                .andExpect(jsonPath("$[0].email").value(testUserDto.getEmail()));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(testUserDto.getName()))
                .andExpect(jsonPath("$.email").value(testUserDto.getEmail()));

        verify(userService).getUserById(1L);
    }

    @Test
    @WithMockUser
    void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(1L);
    }

    @Test
    @WithMockUser
    void createUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(testUserDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(testUserDto.getName()))
                .andExpect(jsonPath("$.email").value(testUserDto.getEmail()));

        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    @WithMockUser
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(testUserDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(testUserDto.getName()))
                .andExpect(jsonPath("$.email").value(testUserDto.getEmail()));

        verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    @WithMockUser
    void updateUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDto.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    @WithMockUser
    void deleteUser_WhenUserExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser
    void deleteUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User not found")).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(1L);
    }
} 