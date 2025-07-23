package com.jsonplaceholder.api.service;

import com.jsonplaceholder.api.dto.PostDto;
import com.jsonplaceholder.api.model.Post;
import com.jsonplaceholder.api.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Post post = convertToEntity(postDto);
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    @Override
    public PostDto updatePost(Long id, PostDto postDto) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found with id: " + id);
        }
        Post post = convertToEntity(postDto);
        post.setId(id);
        Post updatedPost = postRepository.save(post);
        return convertToDto(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    private PostDto convertToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        return dto;
    }

    private Post convertToEntity(PostDto dto) {
        Post post = new Post();
        post.setId(dto.getId());
        post.setUserId(dto.getUserId());
        post.setTitle(dto.getTitle());
        post.setBody(dto.getBody());
        return post;
    }
} 