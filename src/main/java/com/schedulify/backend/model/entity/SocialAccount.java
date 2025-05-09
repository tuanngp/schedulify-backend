package com.schedulify.backend.model.entity;

import com.schedulify.backend.model.enums.Platform;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a connected social media account.
 */
@Entity
@Table(name = "social_accounts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SocialAccount extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;
    
    @Column(nullable = false)
    private String accountId;
    
    @Column(nullable = false)
    private String accountName;
    
    @Column(length = 1000)
    private String accessToken;
    
    @Column(length = 1000)
    private String refreshToken;
    
    @Column
    private LocalDateTime tokenExpiryDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
} 