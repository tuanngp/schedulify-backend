package com.schedulify.backend.service.socialmedia;

import com.schedulify.backend.model.entity.MediaAttachment;
import com.schedulify.backend.model.enums.MediaType;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.entity.PlatformPost;
import com.schedulify.backend.model.entity.SocialAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of SocialMediaProvider for Facebook.
 */
@Service
public class FacebookProvider extends AbstractSocialMediaProvider {
    
    @Value("${social.facebook.app-id}")
    private String appId;
    
    @Value("${social.facebook.app-secret}")
    private String appSecret;
    
    @Value("${social.facebook.api-version}")
    private String apiVersion;
    
    @Value("${social.facebook.graph-url:https://graph.facebook.com}")
    private String graphUrl;

    public FacebookProvider(RestTemplate restTemplate) {
        super(restTemplate);
    }
    
    @Override
    public Platform getPlatformType() {
        return Platform.FACEBOOK;
    }
    
    @Override
    public Map<String, String> authenticate(String authCode, String redirectUri) {
        Map<String, String> result = new HashMap<>();
        
        try {
            // Exchange auth code for access token
            String tokenUrl = String.format(
                "%s/%s/oauth/access_token?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s",
                graphUrl, apiVersion, appId, appSecret, authCode, redirectUri
            );
            
            ResponseEntity<Map> tokenResponse = restTemplate.getForEntity(tokenUrl, Map.class);
            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get access token from Facebook");
            }
            
            String accessToken = (String) tokenResponse.getBody().get("access_token");
            result.put("accessToken", accessToken);
            
            // Get account details
            String meUrl = String.format("%s/%s/me?fields=id,name&access_token=%s", 
                graphUrl, apiVersion, accessToken);
            ResponseEntity<Map> meResponse = restTemplate.getForEntity(meUrl, Map.class);
            
            if (!meResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get account details from Facebook");
            }
            
            result.put("accountId", (String) meResponse.getBody().get("id"));
            result.put("accountName", (String) meResponse.getBody().get("name"));
            
            // Get long-lived token
            String longTokenUrl = String.format(
                "%s/%s/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                graphUrl, apiVersion, appId, appSecret, accessToken
            );
            
            ResponseEntity<Map> longTokenResponse = restTemplate.getForEntity(longTokenUrl, Map.class);
            if (!longTokenResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get long-lived token from Facebook");
            }
            
            String longLivedToken = (String) longTokenResponse.getBody().get("access_token");
            Integer expiresIn = (Integer) longTokenResponse.getBody().get("expires_in");
            
            result.put("accessToken", longLivedToken);
            result.put("expiresIn", String.valueOf(expiresIn));
            
            return result;
        } catch (Exception e) {
            logger.error("Error authenticating with Facebook: {}", e.getMessage(), e);
            throw new RuntimeException("Facebook authentication failed", e);
        }
    }
    
    @Override
    public SocialAccount refreshToken(SocialAccount socialAccount) {
        try {
            String refreshUrl = String.format(
                "%s/%s/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                graphUrl, apiVersion, appId, appSecret, socialAccount.getAccessToken()
            );
            
            ResponseEntity<Map> response = restTemplate.getForEntity(refreshUrl, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to refresh Facebook token");
            }
            
            String newToken = (String) response.getBody().get("access_token");
            Integer expiresIn = (Integer) response.getBody().get("expires_in");
            
            socialAccount.setAccessToken(newToken);
            socialAccount.setTokenExpiryDate(LocalDateTime.now().plusSeconds(expiresIn));
            
            return socialAccount;
        } catch (Exception e) {
            logger.error("Error refreshing Facebook token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refresh Facebook token", e);
        }
    }
    
    @Override
    protected PlatformPost doPublishPost(PlatformPost platformPost, List<MediaAttachment> attachments) {
        SocialAccount account = platformPost.getSocialAccount();
        String content = platformPost.getPlatformContent();
        
        try {
            String postUrl = String.format(
                "%s/%s/%s/feed",
                graphUrl, apiVersion, account.getAccountId()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("message", content);
            map.add("access_token", account.getAccessToken());
            
            // Handle scheduled posts
            if (platformPost.getPost().getScheduledTime() != null && 
                platformPost.getPost().getScheduledTime().isAfter(LocalDateTime.now())) {
                long scheduledTime = platformPost.getPost().getScheduledTime()
                    .toEpochSecond(ZoneOffset.UTC);
                map.add("published", "false");
                map.add("scheduled_publish_time", String.valueOf(scheduledTime));
            }
            
            // Handle attachments 
            if (!attachments.isEmpty()) {
                // For simplicity, we're just handling the first attachment here
                // A complete implementation would handle multiple attachments differently
                MediaAttachment attachment = attachments.get(0);
                
                if (attachment.getMediaType() == MediaType.IMAGE) {
                    map.add("link", attachment.getFileUrl());
                } else if (attachment.getMediaType() == MediaType.VIDEO) {
                    // For videos, we would need to use a different endpoint and approach
                    // Simplified for this example
                    map.add("description", content);
                    map.add("file_url", attachment.getFileUrl());
                }
            }
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                postUrl, HttpMethod.POST, request, Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to publish post to Facebook");
            }
            
            String postId = (String) response.getBody().get("id");
            platformPost.setPlatformPostId(postId);
            
            // Set post URL
            String postPageUrl = String.format("https://facebook.com/%s", postId);
            platformPost.setPlatformPostUrl(postPageUrl);
            
            return platformPost;
        } catch (Exception e) {
            logger.error("Error publishing to Facebook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish to Facebook", e);
        }
    }
    
    @Override
    public boolean deletePost(PlatformPost platformPost) {
        try {
            String deleteUrl = String.format(
                "%s/%s/%s?access_token=%s",
                graphUrl, apiVersion, platformPost.getPlatformPostId(),
                platformPost.getSocialAccount().getAccessToken()
            );
            
            ResponseEntity<Map> response = restTemplate.exchange(
                deleteUrl, HttpMethod.DELETE, null, Map.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("Error deleting Facebook post: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String formatContent(String content, List<MediaAttachment> mediaAttachments) {
        // Facebook-specific content formatting could be implemented here
        // For now, we just return the content as is
        return content;
    }
    
    @Override
    public Map<String, Object> getPlatformConstraints() {
        Map<String, Object> constraints = new HashMap<>();
        constraints.put("maxTextLength", 63206); // Facebook's current limit
        constraints.put("maxAttachments", 10);
        constraints.put("supportedMediaTypes", Arrays.asList(
            MediaType.IMAGE, MediaType.VIDEO, MediaType.GIF));
        return constraints;
    }
}