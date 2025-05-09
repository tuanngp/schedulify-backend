package com.schedulify.backend.model.dto.response;

import com.schedulify.backend.model.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for MediaAttachment entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAttachmentDTO {
    
    private Long id;
    private String filename;
    private String fileUrl;
    private String thumbnailUrl;
    private long fileSize;
    private MediaType mediaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 