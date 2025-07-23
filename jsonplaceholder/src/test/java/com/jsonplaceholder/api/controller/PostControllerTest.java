package com.jsonplaceholder.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsonplaceholder.api.dto.PostDto;
import com.jsonplaceholder.api.security.JwtAuthenticationFilter;
import com.jsonplaceholder.api.security.JwtTokenProvider;
import com.jsonplaceholder.api.service.PostService;
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

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private PostDto testPostDto;

    @BeforeEach
    void setUp() {
        testPostDto = new PostDto();
        testPostDto.setId(1L);
        testPostDto.setTitle("Test Post");
        testPostDto.setBody("This is a test post body");
        testPostDto.setUserId(1L);
    }

    @Test
    void getAllPosts_ShouldReturnPosts() throws Exception {
        List<PostDto> posts = Arrays.asList(testPostDto);
        when(postService.getAllPosts()).thenReturn(posts);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value(testPostDto.getTitle()))
                .andExpect(jsonPath("$[0].body").value(testPostDto.getBody()));

        verify(postService).getAllPosts();
    }

    @Test
    void getPostById_WhenPostExists_ShouldReturnPost() throws Exception {
        when(postService.getPostById(1L)).thenReturn(testPostDto);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(testPostDto.getTitle()))
                .andExpect(jsonPath("$.body").value(testPostDto.getBody()));

        verify(postService).getPostById(1L);
    }

    @Test
    void getPostById_WhenPostDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(postService.getPostById(1L)).thenThrow(new EntityNotFoundException("Post not found"));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isNotFound());

        verify(postService).getPostById(1L);
    }

    @Test
    @WithMockUser
    void createPost_ShouldReturnCreatedPost() throws Exception {
        when(postService.createPost(any(PostDto.class))).thenReturn(testPostDto);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPostDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(testPostDto.getTitle()))
                .andExpect(jsonPath("$.body").value(testPostDto.getBody()));

        verify(postService).createPost(any(PostDto.class));
    }

    @Test
    @WithMockUser
    void updatePost_WhenPostExists_ShouldReturnUpdatedPost() throws Exception {
        when(postService.updatePost(eq(1L), any(PostDto.class))).thenReturn(testPostDto);

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPostDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(testPostDto.getTitle()))
                .andExpect(jsonPath("$.body").value(testPostDto.getBody()));

        verify(postService).updatePost(eq(1L), any(PostDto.class));
    }

    @Test
    @WithMockUser
    void updatePost_WhenPostDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(postService.updatePost(eq(1L), any(PostDto.class)))
                .thenThrow(new EntityNotFoundException("Post not found"));

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPostDto)))
                .andExpect(status().isNotFound());

        verify(postService).updatePost(eq(1L), any(PostDto.class));
    }

    @Test
    @WithMockUser
    void deletePost_WhenPostExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(1L);
    }

    @Test
    @WithMockUser
    void deletePost_WhenPostDoesNotExist_ShouldReturnNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Post not found")).when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNotFound());

        verify(postService).deletePost(1L);
    }
} 