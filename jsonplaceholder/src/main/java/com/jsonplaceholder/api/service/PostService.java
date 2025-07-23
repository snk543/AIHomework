package com.jsonplaceholder.api.service;

import com.jsonplaceholder.api.dto.PostDto;
import java.util.List;

public interface PostService {
    List<PostDto> getAllPosts();
    PostDto getPostById(Long id);
    PostDto createPost(PostDto postDto);
    PostDto updatePost(Long id, PostDto postDto);
    void deletePost(Long id);
} 