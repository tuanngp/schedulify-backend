package com.schedulify.backend.service.socialmedia;

import com.schedulify.backend.model.entity.MediaAttachment;
import com.schedulify.backend.model.enums.MediaType;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.entity.PlatformPost;
import com.schedulify.backend.model.enums.PostStatus;
import com.schedulify.backend.model.entity.SocialAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of the SocialMediaProvider interface.
 * Handles common functionality across different social media platforms.
 */
public abstract class AbstractSocialMediaProvider implements SocialMediaProvider {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final RestTemplate restTemplate;
    
    protected AbstractSocialMediaProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PlatformPost publishPost(PlatformPost platformPost, List<MediaAttachment> attachments) {
        try {
            PlatformPost result = doPublishPost(platformPost, attachments);
            result.setStatus(PostStatus.PUBLISHED);
            result.setPublishedAt(LocalDateTime.now());
            return result;
        } catch (Exception e) {
            logger.error("Error publishing post to {}: {}", getPlatformType(), e.getMessage(), e);
            platformPost.setStatus(PostStatus.FAILED);
            platformPost.setErrorMessage(e.getMessage());
            return platformPost;
        }
    }
    
    /**
     * Platform-specific implementation of post publishing.
     */
    protected abstract PlatformPost doPublishPost(PlatformPost platformPost, List<MediaAttachment> attachments);
    
    @Override
    public String formatContent(String content, List<MediaAttachment> mediaAttachments) {
        // Default implementation returns original content
        // Platform-specific implementations should override this
        return content;
    }
    
    @Override
    public Map<String, Object> validateContent(String content, List<MediaAttachment> mediaAttachments) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> constraints = getPlatformConstraints();
        
        // Check content length
        Integer maxLength = (Integer) constraints.get("maxTextLength");
        if (maxLength != null && content.length() > maxLength) {
            result.put("isValid", false);
            result.put("message", "Content exceeds maximum length of " + maxLength + " characters");
            return result;
        }
        
        // Check media attachments
        @SuppressWarnings("unchecked")
        List<MediaType> supportedTypes = (List<MediaType>) constraints.get("supportedMediaTypes");
        Integer maxAttachments = (Integer) constraints.get("maxAttachments");
        
        if (maxAttachments != null && mediaAttachments.size() > maxAttachments) {
            result.put("isValid", false);
            result.put("message", "Too many attachments. Maximum allowed: " + maxAttachments);
            return result;
        }
        
        if (supportedTypes != null) {
            for (MediaAttachment attachment : mediaAttachments) {
                if (!supportedTypes.contains(attachment.getMediaType())) {
                    result.put("isValid", false);
                    result.put("message", "Unsupported media type: " + attachment.getMediaType());
                    return result;
                }
            }
        }
        
        // If we get here, content is valid
        result.put("isValid", true);
        result.put("message", "Content is valid for " + getPlatformType());
        return result;
    }
} 