package com.jsonplaceholder.api.service;

import com.jsonplaceholder.api.dto.UserDto;
import com.jsonplaceholder.api.model.User;
import com.jsonplaceholder.api.repository.UserRepository;
import com.jsonplaceholder.api.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPhone("1234567890");
        testUser.setWebsite("www.test.com");

        testUserDto = new UserDto();
        testUserDto.setName("Test User");
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setPhone("1234567890");
        testUserDto.setWebsite("www.test.com");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getName(), result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(testUserDto);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUser(1L, testUserDto);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).existsById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, testUserDto));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }
} 