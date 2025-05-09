package com.schedulify.backend.model.entity;

import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.enums.PostStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a platform-specific instance of a post.
 * This allows tracking different post IDs and statuses across platforms.
 */
@Entity
@Table(name = "platform_posts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PlatformPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_account_id", nullable = false)
    private SocialAccount socialAccount;
    
    @Column(columnDefinition = "TEXT")
    private String platformContent;
    
    @Column
    private String platformPostId;
    
    @Column
    private String platformPostUrl;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.DRAFT;
    
    @Column
    private String errorMessage;
    
    @Column
    private LocalDateTime publishedAt;
} 