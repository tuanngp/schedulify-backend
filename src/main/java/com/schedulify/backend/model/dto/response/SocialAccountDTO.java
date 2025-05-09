package com.schedulify.backend.model.dto.response;

import com.schedulify.backend.model.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for SocialAccount entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccountDTO {
    
    private Long id;
    private Platform platform;
    private String accountId;
    private String accountName;
    private boolean active;
    private LocalDateTime tokenExpiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}