package com.schedulify.backend.model.dto.response;

import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object for Post entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    
    private Long id;
    private String title;
    private String content;
    private String sourceLink;
    private Set<Platform> targetPlatforms = new HashSet<>();
    private List<MediaAttachmentDTO> mediaAttachments = new ArrayList<>();
    private List<PlatformPostDTO> platformPosts = new ArrayList<>();
    private PostStatus status;
    private LocalDateTime scheduledTime;
    private Long authorId;
    private String authorName;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 