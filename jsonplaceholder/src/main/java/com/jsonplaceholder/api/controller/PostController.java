package com.jsonplaceholder.api.controller;

import com.jsonplaceholder.api.dto.PostDto;
import com.jsonplaceholder.api.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postService.getPostById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {
        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto postDto) {
        try {
            return ResponseEntity.ok(postService.updatePost(id, postDto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 