package com.schedulify.backend.service;

import com.schedulify.backend.model.dto.BaseApiResponse;
import com.schedulify.backend.model.dto.response.SocialAccountDTO;
import com.schedulify.backend.model.entity.SocialAccount;
import com.schedulify.backend.model.entity.User;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.repository.SocialAccountRepository;
import com.schedulify.backend.service.socialmedia.SocialMediaIntegrationService;
import com.schedulify.backend.service.socialmedia.SocialMediaProvider;
import com.schedulify.backend.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing social media accounts.
 */
@Service
public class SocialAccountService {
    
    private final SocialAccountRepository socialAccountRepository;
    private final SocialMediaIntegrationService socialMediaIntegrationService;
    private final UserService userService;
    
    public SocialAccountService(
            SocialAccountRepository socialAccountRepository,
            SocialMediaIntegrationService socialMediaIntegrationService,
            UserService userService) {
        this.socialAccountRepository = socialAccountRepository;
        this.socialMediaIntegrationService = socialMediaIntegrationService;
        this.userService = userService;
    }

    public ResponseEntity<BaseApiResponse<List<SocialAccountDTO>>> getCurrentUserAccounts() {
        try {
            User currentUser = userService.getCurrentUser();
            List<SocialAccountDTO> list = socialAccountRepository.findByUserAndActiveTrue(currentUser)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            return ResponseUtils.ok(list);
        } catch (Exception e) {
            return ResponseUtils.fail("Failed to fetch social accounts: " + e.getMessage());
        }
    }

    public ResponseEntity<BaseApiResponse<SocialAccountDTO>> getAccountById(Long id) {
        SocialAccount account = findAccountById(id);
        return ResponseUtils.ok(mapToDTO(account));
    }

    public ResponseEntity<BaseApiResponse<Map<String, String>>> getAuthUrl(Platform platform) {
        // In a real implementation, this would generate platform-specific OAuth URLs
        // For simplicity, we'll return a stub URL
        Map<String, String> result = new HashMap<>();
        result.put("authUrl", "https://example.com/auth/" + platform.name().toLowerCase());
        return ResponseUtils.ok(result);
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<SocialAccountDTO>> authenticate(Platform platform, String code, String redirectUri) {
        Map<String, String> authResult = socialMediaIntegrationService.authenticate(
                platform, code, redirectUri);
        
        User currentUser = userService.getCurrentUser();
        
        // Check if account already exists
        SocialAccount existingAccount = socialAccountRepository
                .findByPlatformAndAccountId(platform, authResult.get("accountId"))
                .orElse(null);
        
        if (existingAccount != null) {
            // Update existing account
            existingAccount.setAccessToken(authResult.get("accessToken"));
            existingAccount.setRefreshToken(authResult.get("refreshToken"));
            existingAccount.setTokenExpiryDate(LocalDateTime.now().plusSeconds(
                    Long.parseLong(authResult.getOrDefault("expiresIn", "3600"))));
            existingAccount.setActive(true);
            existingAccount.setAccountName(authResult.getOrDefault("accountName", "Unknown"));
            
            return ResponseUtils.ok(mapToDTO(socialAccountRepository.save(existingAccount)));
        } else {
            // Create new account
            SocialAccount newAccount = new SocialAccount();
            newAccount.setPlatform(platform);
            newAccount.setAccountId(authResult.get("accountId"));
            newAccount.setAccountName(authResult.getOrDefault("accountName", "Unknown"));
            newAccount.setAccessToken(authResult.get("accessToken"));
            newAccount.setRefreshToken(authResult.getOrDefault("refreshToken", null));
            newAccount.setTokenExpiryDate(LocalDateTime.now().plusSeconds(
                    Long.parseLong(authResult.getOrDefault("expiresIn", "3600"))));
            newAccount.setActive(true);
            newAccount.setUser(currentUser);
            
            return ResponseUtils.ok(mapToDTO(socialAccountRepository.save(newAccount)));
        }
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<Void>> disconnectAccount(Long id) {
        SocialAccount account = findAccountById(id);
        account.setActive(false);
        socialAccountRepository.save(account);
        return ResponseUtils.ok(null,"Account disconnected successfully");
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<SocialAccountDTO>> refreshToken(Long id) {
        SocialAccount account = findAccountById(id);
        
        // Call the platform-specific token refresh logic
        SocialAccount refreshedAccount = socialMediaIntegrationService.refreshToken(account);
        
        // Save the updated account
        return ResponseUtils.ok(mapToDTO(socialAccountRepository.save(refreshedAccount)));
    }

    public ResponseEntity<BaseApiResponse<List<Map<String, Object>>>> getSupportedPlatforms() {
        Map<Platform, SocialMediaProvider> providers = socialMediaIntegrationService.getAllProviders();
        
        List<Map<String, Object>> result = new ArrayList<>();
        providers.forEach((platform, provider) -> {
            Map<String, Object> platformInfo = new HashMap<>();
            platformInfo.put("name", platform.name());
            platformInfo.put("constraints", provider.getPlatformConstraints());
            result.add(platformInfo);
        });
        
        return ResponseUtils.ok(result);
    }

    private SocialAccount findAccountById(Long id) {
        User currentUser = userService.getCurrentUser();
        return socialAccountRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Social account not found"));
    }

    private SocialAccountDTO mapToDTO(SocialAccount account) {
        return SocialAccountDTO.builder()
                .id(account.getId())
                .platform(account.getPlatform())
                .accountId(account.getAccountId())
                .accountName(account.getAccountName())
                .active(account.isActive())
                .tokenExpiryDate(account.getTokenExpiryDate())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
} 