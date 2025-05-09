package com.schedulify.backend.model.entity;

import com.schedulify.backend.model.enums.MediaType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity representing a media attachment for a social media post.
 */
@Entity
@Table(name = "media_attachments")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MediaAttachment extends BaseEntity {
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String fileUrl;
    
    @Column
    private String thumbnailUrl;
    
    @Column(nullable = false)
    private long fileSize;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
} 