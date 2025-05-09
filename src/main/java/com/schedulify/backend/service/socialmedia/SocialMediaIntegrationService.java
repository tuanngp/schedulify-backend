package com.schedulify.backend.service.socialmedia;

import com.schedulify.backend.model.entity.MediaAttachment;
import com.schedulify.backend.model.entity.PlatformPost;
import com.schedulify.backend.model.entity.Post;
import com.schedulify.backend.model.entity.SocialAccount;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.enums.PostStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing social media integrations across all platforms.
 * Acts as a facade for the individual platform-specific providers.
 */
@Service
public class SocialMediaIntegrationService {
    
    private final Map<Platform, SocialMediaProvider> providers;
    
    public SocialMediaIntegrationService(List<SocialMediaProvider> providerList) {
        // Create a map of providers by platform type for easy lookup
        providers = new HashMap<>();
        providerList.forEach(provider -> providers.put(provider.getPlatformType(), provider));
    }
    
    /**
     * Get a provider for a specific platform.
     */
    public Optional<SocialMediaProvider> getProvider(Platform platform) {
        return Optional.ofNullable(providers.get(platform));
    }
    
    /**
     * Get all available platform providers.
     */
    public Map<Platform, SocialMediaProvider> getAllProviders() {
        return new HashMap<>(providers);
    }
    
    /**
     * Authenticate with a specific platform.
     */
    public Map<String, String> authenticate(Platform platform, String authCode, String redirectUri) {
        SocialMediaProvider provider = getProviderOrThrow(platform);
        return provider.authenticate(authCode, redirectUri);
    }
    
    /**
     * Refresh a social account token.
     */
    public SocialAccount refreshToken(SocialAccount socialAccount) {
        SocialMediaProvider provider = getProviderOrThrow(socialAccount.getPlatform());
        return provider.refreshToken(socialAccount);
    }
    
    /**
     * Publish a post to a specific platform.
     */
    public PlatformPost publishPost(PlatformPost platformPost, List<MediaAttachment> attachments) {
        SocialMediaProvider provider = getProviderOrThrow(platformPost.getPlatform());
        return provider.publishPost(platformPost, attachments);
    }
    
    /**
     * Delete a post from a specific platform.
     */
    public boolean deletePost(PlatformPost platformPost) {
        SocialMediaProvider provider = getProviderOrThrow(platformPost.getPlatform());
        return provider.deletePost(platformPost);
    }
    
    /**
     * Format content for a specific platform.
     */
    public String formatContent(Platform platform, String content, List<MediaAttachment> attachments) {
        SocialMediaProvider provider = getProviderOrThrow(platform);
        return provider.formatContent(content, attachments);
    }
    
    /**
     * Validate content for a specific platform.
     */
    public Map<String, Object> validateContent(Platform platform, String content, List<MediaAttachment> attachments) {
        SocialMediaProvider provider = getProviderOrThrow(platform);
        return provider.validateContent(content, attachments);
    }
    
    /**
     * Get validation constraints for a specific platform.
     */
    public Map<String, Object> getPlatformConstraints(Platform platform) {
        SocialMediaProvider provider = getProviderOrThrow(platform);
        return provider.getPlatformConstraints();
    }
    
    /**
     * Get validation constraints for all supported platforms.
     */
    public Map<Platform, Map<String, Object>> getAllPlatformConstraints() {
        Map<Platform, Map<String, Object>> result = new HashMap<>();
        providers.forEach((platform, provider) -> 
            result.put(platform, provider.getPlatformConstraints()));
        return result;
    }
    
    /**
     * Publish a post to multiple platforms.
     */
    public List<PlatformPost> publishToMultiplePlatforms(Post post, List<PlatformPost> platformPosts, List<MediaAttachment> attachments) {
        return platformPosts.stream()
            .map(platformPost -> {
                try {
                    SocialMediaProvider provider = getProviderOrThrow(platformPost.getPlatform());
                    return provider.publishPost(platformPost, attachments);
                } catch (Exception e) {
                    platformPost.setStatus(PostStatus.FAILED);
                    platformPost.setErrorMessage(e.getMessage());
                    return platformPost;
                }
            })
            .collect(Collectors.toList());
    }
    
    private SocialMediaProvider getProviderOrThrow(Platform platform) {
        return getProvider(platform)
            .orElseThrow(() -> new IllegalArgumentException("No provider available for platform: " + platform));
    }
} 