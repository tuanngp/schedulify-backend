package com.schedulify.backend.controller;

import com.schedulify.backend.model.dto.response.PostDTO;
import com.schedulify.backend.model.dto.BaseApiResponse;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST controller for managing social media posts.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<Page<PostDTO>>> getPosts(Pageable pageable) {
        return postService.getPosts(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<PostDTO>> getPost(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<PostDTO>> createPost(@RequestBody PostDTO postDTO) {
        return postService.createPost(postDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<PostDTO>> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        return postService.updatePost(id, postDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<Void>> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    @PostMapping("/{id}/media")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<PostDTO>> uploadMedia(@PathVariable Long id, @RequestParam MultipartFile file) {
        return postService.addMedia(id, file);
    }

    @DeleteMapping("/{postId}/media/{mediaId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<PostDTO>> deleteMedia(@PathVariable Long postId, @PathVariable Long mediaId) {
        return postService.removeMedia(postId, mediaId);
    }

    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<PostDTO>> schedulePost(@PathVariable Long id, @RequestBody Map<String, String> scheduleRequest) {
        return
                postService.schedulePost(id, scheduleRequest.get("scheduledTime"));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<PostDTO>> publishPost(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @PostMapping("/validate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<Map<Platform, Map<String, Object>>>> validateContent(@RequestBody PostDTO postDTO) {
        return postService.validateContent(postDTO);
    }

    @PostMapping("/format/{platform}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CONTENT_MANAGER', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<String>> formatContent(
            @PathVariable Platform platform,
            @RequestBody Map<String, String> contentRequest) {
        return
                postService.formatContent(platform, contentRequest.get("content"));
    }
} 