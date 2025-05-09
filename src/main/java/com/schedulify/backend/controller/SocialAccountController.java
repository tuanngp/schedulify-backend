package com.schedulify.backend.controller;

import com.schedulify.backend.model.dto.BaseApiResponse;
import com.schedulify.backend.model.dto.response.SocialAccountDTO;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.service.SocialAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social-accounts")
public class SocialAccountController {
    
    private final SocialAccountService socialAccountService;
    
    public SocialAccountController(SocialAccountService socialAccountService) {
        this.socialAccountService = socialAccountService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<List<SocialAccountDTO>>> getUserAccounts() {
        return socialAccountService.getCurrentUserAccounts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<SocialAccountDTO>> getAccount(@PathVariable Long id) {
        return socialAccountService.getAccountById(id);
    }

    @GetMapping("/auth-url/{platform}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<Map<String, String>>> getAuthUrl(@PathVariable Platform platform) {
        return socialAccountService.getAuthUrl(platform);
    }

    @PostMapping("/authenticate/{platform}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<SocialAccountDTO>> authenticate(
            @PathVariable Platform platform,
            @RequestParam String code,
            @RequestParam String redirectUri) {
        return socialAccountService.authenticate(platform, code, redirectUri);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<Void>> disconnectAccount(@PathVariable Long id) {
        return socialAccountService.disconnectAccount(id);
    }

    @PostMapping("/{id}/refresh-token")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_MEDIA_MANAGER')")
    public ResponseEntity<BaseApiResponse<SocialAccountDTO>> refreshToken(@PathVariable Long id) {
        return socialAccountService.refreshToken(id);
    }

    @GetMapping("/platforms")
    public ResponseEntity<BaseApiResponse<List<Map<String, Object>>>> getSupportedPlatforms() {
        return socialAccountService.getSupportedPlatforms();
    }
} 