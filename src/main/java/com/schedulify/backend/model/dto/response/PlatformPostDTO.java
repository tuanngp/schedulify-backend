package com.schedulify.backend.model.dto.response;

import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for PlatformPost entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformPostDTO {
    
    private Long id;
    private Long postId;
    private Platform platform;
    private Long socialAccountId;
    private String socialAccountName;
    private String platformContent;
    private String platformPostId;
    private String platformPostUrl;
    private PostStatus status;
    private String errorMessage;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 