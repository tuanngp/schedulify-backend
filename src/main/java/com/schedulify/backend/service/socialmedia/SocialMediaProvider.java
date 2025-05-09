package com.schedulify.backend.service.socialmedia;

import com.schedulify.backend.model.entity.MediaAttachment;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.entity.PlatformPost;
import com.schedulify.backend.model.entity.SocialAccount;

import java.util.List;
import java.util.Map;

/**
 * Interface for social media platform integrations.
 * Each supported platform should implement this interface.
 */
public interface SocialMediaProvider {
    
    /**
     * Get the type of platform this provider handles.
     * @return The platform type.
     */
    Platform getPlatformType();
    
    /**
     * Authenticate with the platform and retrieve access tokens.
     * @param authCode Authorization code from OAuth flow
     * @param redirectUri Redirect URI used in the OAuth flow
     * @return Map containing account details and tokens
     */
    Map<String, String> authenticate(String authCode, String redirectUri);
    
    /**
     * Refresh the access token for a social account.
     * @param socialAccount Account to refresh
     * @return Updated account with new tokens
     */
    SocialAccount refreshToken(SocialAccount socialAccount);
    
    /**
     * Publish a post to the platform.
     * @param platformPost The post to publish
     * @param attachments Media attachments to include
     * @return Updated platform post with platform-specific ID and URL
     */
    PlatformPost publishPost(PlatformPost platformPost, List<MediaAttachment> attachments);
    
    /**
     * Delete a post from the platform.
     * @param platformPost The post to delete
     * @return True if deletion succeeded
     */
    boolean deletePost(PlatformPost platformPost);
    
    /**
     * Format a post content for the specific platform.
     * @param content Original content
     * @param mediaAttachments Media attachments
     * @return Platform-optimized content
     */
    String formatContent(String content, List<MediaAttachment> mediaAttachments);
    
    /**
     * Check if a given content is valid for the platform.
     * @param content The content to validate
     * @param mediaAttachments Media attachments to include
     * @return Map of validation results (isValid and message)
     */
    Map<String, Object> validateContent(String content, List<MediaAttachment> mediaAttachments);
    
    /**
     * Get validation rules for this platform.
     * @return Map of platform constraints (max text length, supported media types, etc.)
     */
    Map<String, Object> getPlatformConstraints();
} 