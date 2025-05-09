package com.schedulify.backend.service;

import com.schedulify.backend.mapper.MediaAttachmentMapper;
import com.schedulify.backend.mapper.PlatformPostMapper;
import com.schedulify.backend.mapper.PostMapper;
import com.schedulify.backend.model.dto.response.MediaAttachmentDTO;
import com.schedulify.backend.model.dto.response.PlatformPostDTO;
import com.schedulify.backend.model.dto.response.PostDTO;
import com.schedulify.backend.model.dto.BaseApiResponse;
import com.schedulify.backend.model.entity.*;
import com.schedulify.backend.model.enums.MediaType;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.enums.PostStatus;
import com.schedulify.backend.repository.MediaAttachmentRepository;
import com.schedulify.backend.repository.PlatformPostRepository;
import com.schedulify.backend.repository.PostRepository;
import com.schedulify.backend.repository.SocialAccountRepository;
import com.schedulify.backend.service.socialmedia.SocialMediaIntegrationService;
import com.schedulify.backend.utils.ResponseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final MediaAttachmentRepository mediaAttachmentRepository;
    private final PlatformPostRepository platformPostRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final SocialMediaIntegrationService socialMediaIntegrationService;
    private final UserService userService;

    private final PostMapper postMapper;
    private final MediaAttachmentMapper mediaAttachmentMapper;
    private final PlatformPostMapper platformPostMapper;

    private final Path fileStorageLocation;

    public PostService(
            PostRepository postRepository,
            MediaAttachmentRepository mediaAttachmentRepository,
            PlatformPostRepository platformPostRepository,
            SocialAccountRepository socialAccountRepository,
            SocialMediaIntegrationService socialMediaIntegrationService,
            UserService userService,
            PostMapper postMapper,
            MediaAttachmentMapper mediaAttachmentMapper,
            PlatformPostMapper platformPostMapper) {
        this.postRepository = postRepository;
        this.mediaAttachmentRepository = mediaAttachmentRepository;
        this.platformPostRepository = platformPostRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.socialMediaIntegrationService = socialMediaIntegrationService;
        this.userService = userService;
        this.postMapper = postMapper;
        this.mediaAttachmentMapper = mediaAttachmentMapper;
        this.platformPostMapper = platformPostMapper;

        // Set up file storage location
        this.fileStorageLocation = Paths.get("uploads")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create the directory where uploaded files will be stored", e);
        }
    }

    public ResponseEntity<BaseApiResponse<Page<PostDTO>>> getPosts(Pageable pageable) {
        try {
            User currentUser = userService.getCurrentUser();
            Page<Post> posts = postRepository.findByAuthor(currentUser, pageable);
            Page<PostDTO> postDTOs = posts.map(postMapper::toDTO);
            return ResponseUtils.ok(postDTOs, "Posts retrieved successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<BaseApiResponse<PostDTO>> getPostById(Long id) {
        try {
            User currentUser = userService.getCurrentUser();
            Post post = postRepository.findByIdAndAuthor(id, currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));
            return ResponseUtils.ok(postMapper.toDTO(post), "Post retrieved successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<PostDTO>> createPost(PostDTO postDTO) {
        try {
            User currentUser = userService.getCurrentUser();

            Post post = postMapper.toEntity(postDTO);
            post.setStatus(PostStatus.DRAFT);
            post.setAuthor(currentUser);

            Post savedPost = postRepository.save(post);

            return ResponseUtils.ok(mapToDTO(savedPost), "Post created successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<PostDTO>> updatePost(Long id, PostDTO postDTO) {
        try {
            User currentUser = userService.getCurrentUser();
            Post existingPost = postRepository.findByIdAndAuthor(id, currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));

            // Only allow updates if post is in DRAFT status
            if (existingPost.getStatus() != PostStatus.DRAFT) {
                throw new IllegalStateException("Cannot update a post that is not in DRAFT status");
            }

            Post post = postMapper.toEntity(postDTO);
            post.setId(existingPost.getId());
            post.setAuthor(currentUser);
            post.setStatus(existingPost.getStatus());
            post.setCreatedAt(existingPost.getCreatedAt());
            post.setCreatedBy(existingPost.getCreatedBy());

            Post savedPost = postRepository.save(post);

            return ResponseUtils.ok(mapToDTO(savedPost), "Post updated successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<Void>> deletePost(Long id) {
        try {
            User currentUser = userService.getCurrentUser();
            Post post = postRepository.findByIdAndAuthor(id, currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));

            // Only allow deletion if post is in DRAFT or CANCELED status
            if (post.getStatus() != PostStatus.DRAFT && post.getStatus() != PostStatus.CANCELED) {
                throw new IllegalStateException("Cannot delete a post that is not in DRAFT or CANCELED status");
            }

            postRepository.delete(post);
            return ResponseUtils.ok(null, "Post deleted successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<PostDTO>> addMedia(Long id, MultipartFile file) {

        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findByIdAndAuthor(id, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));

        // Only allow updates if post is in DRAFT status
        if (post.getStatus() != PostStatus.DRAFT) {
            throw new IllegalStateException("Cannot add media to a post that is not in DRAFT status");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            MediaAttachmentDTO mediaAttachmentDTO = MediaAttachmentDTO.builder()
                    .filename(originalFilename)
                    .fileUrl("/uploads/" + uniqueFilename)
                    .fileSize(file.getSize())
                    .mediaType(determineMediaType(fileExtension))
                    .build();

            MediaAttachment mediaAttachment = mediaAttachmentMapper.toEntity(mediaAttachmentDTO);
            mediaAttachment.setPost(post);

            mediaAttachmentRepository.save(mediaAttachment);
            post.getMediaAttachments().add(mediaAttachment);

            return ResponseUtils.ok(mapToDTO(post), "Media added successfully");
        } catch (IOException e) {
            return ResponseUtils.fail("Failed to store file " + originalFilename, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<PostDTO>> removeMedia(Long postId, Long mediaId) {
        try {
            User currentUser = userService.getCurrentUser();
            Post post = postRepository.findByIdAndAuthor(postId, currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

            // Only allow updates if post is in DRAFT status
            if (post.getStatus() != PostStatus.DRAFT) {
                throw new IllegalStateException("Cannot remove media from a post that is not in DRAFT status");
            }

            MediaAttachment mediaAttachment = mediaAttachmentRepository.findById(mediaId)
                    .orElseThrow(() -> new IllegalArgumentException("Media not found: " + mediaId));

            if (!post.getMediaAttachments().contains(mediaAttachment)) {
                throw new IllegalArgumentException("Media does not belong to this post");
            }

            post.getMediaAttachments().remove(mediaAttachment);
            mediaAttachmentRepository.delete(mediaAttachment);

            return ResponseUtils.ok(mapToDTO(post), "Media removed successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<PostDTO>> schedulePost(Long id, String scheduledTimeStr) {
        try {
            User currentUser = userService.getCurrentUser();
            Post post = postRepository.findByIdAndAuthor(id, currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));

            // Only allow scheduling if post is in DRAFT status
            if (post.getStatus() != PostStatus.DRAFT) {
                throw new IllegalStateException("Cannot schedule a post that is not in DRAFT status");
            }

            // Parse scheduled time
            LocalDateTime scheduledTime = LocalDateTime.parse(scheduledTimeStr, DateTimeFormatter.ISO_DATE_TIME);

            // Validate scheduled time is in the future
            if (scheduledTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Scheduled time must be in the future");
            }

            // Create platform-specific posts
            createPlatformPosts(post);

            post.setScheduledTime(scheduledTime);
            post.setStatus(PostStatus.SCHEDULED);

            Post savedPost = postRepository.save(post);

            return ResponseUtils.ok(mapToDTO(savedPost), "Post scheduled successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<BaseApiResponse<PostDTO>> publishPost(Long id) {
        try {
            User currentUser = userService.getCurrentUser();
            Post post = postRepository.findByIdAndAuthor(id, currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));

            // Only allow publishing if post is in DRAFT or SCHEDULED status
            if (post.getStatus() != PostStatus.DRAFT && post.getStatus() != PostStatus.SCHEDULED) {
                throw new IllegalStateException("Cannot publish a post that is not in DRAFT or SCHEDULED status");
            }

            // Create platform-specific posts if they don't exist yet
            if (post.getPlatformPosts().isEmpty()) {
                createPlatformPosts(post);
            }

            // Publish to all platforms
            List<MediaAttachment> attachments = post.getMediaAttachments();
            List<PlatformPost> publishedPlatformPosts = socialMediaIntegrationService.publishToMultiplePlatforms(
                    post, post.getPlatformPosts(), attachments);

            // Update status based on results
            boolean anyFailed = publishedPlatformPosts.stream()
                    .anyMatch(p -> p.getStatus() == PostStatus.FAILED);

            post.setStatus(anyFailed ? PostStatus.FAILED : PostStatus.PUBLISHED);
            post.setPublishedAt(LocalDateTime.now());

            Post savedPost = postRepository.save(post);

            return ResponseUtils.ok(mapToDTO(savedPost), "Post published successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<BaseApiResponse<Map<Platform, Map<String, Object>>>> validateContent(PostDTO postDTO) {
        try {
            List<MediaAttachment> mediaAttachments = postDTO.getMediaAttachments().stream()
                    .map(mediaAttachmentMapper::toEntity)
                    .collect(Collectors.toList());

            Map<Platform, Map<String, Object>> result = new HashMap<>();
            for (Platform platform : postDTO.getTargetPlatforms()) {
                Map<String, Object> validationResult = socialMediaIntegrationService.validateContent(
                        platform, postDTO.getContent(), mediaAttachments);
                result.put(platform, validationResult);
            }

            return ResponseUtils.ok(result, "Content validated successfully");
        } catch (Exception e) {
            return ResponseUtils.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<BaseApiResponse<String>> formatContent(Platform platform, String content) {
        return ResponseUtils.ok(socialMediaIntegrationService.formatContent(platform, content, List.of()), "Content formatted successfully");
    }

    private void createPlatformPosts(Post post) {
        User currentUser = userService.getCurrentUser();

        for (Platform platform : post.getTargetPlatforms()) {
            // Find an active account for this platform
            List<SocialAccount> accounts = socialAccountRepository.findByPlatformAndActiveTrue(platform);

            if (accounts.isEmpty()) {
                throw new IllegalStateException("No active account found for platform: " + platform);
            }

            // Use the first available account (in a real app, you'd allow the user to choose)
            SocialAccount account = accounts.stream()
                    .filter(a -> a.getUser().equals(currentUser))
                    .findFirst()
                    .orElse(accounts.get(0));

            // Format content for this platform
            String platformContent = socialMediaIntegrationService.formatContent(
                    platform, post.getContent(), post.getMediaAttachments());

            PlatformPostDTO platformPostDTO = PlatformPostDTO.builder()
                    .platform(platform)
                    .platformContent(platformContent)
                    .status(PostStatus.DRAFT)
                    .build();

            PlatformPost platformPost = platformPostMapper.toEntity(platformPostDTO);
            platformPost.setPost(post);
            platformPost.setSocialAccount(account);

            platformPostRepository.save(platformPost);
            post.getPlatformPosts().add(platformPost);
        }
    }

    private PostDTO mapToDTO(Post post) {
        List<MediaAttachmentDTO> mediaAttachmentDTOs = post.getMediaAttachments().stream()
                .map(mediaAttachmentMapper::toDTO)
                .collect(Collectors.toList());

        List<PlatformPostDTO> platformPostDTOs = post.getPlatformPosts().stream()
                .map(platformPostMapper::toDTO)
                .collect(Collectors.toList());

        PostDTO postDTO = postMapper.toDTO(post);
        postDTO.setMediaAttachments(mediaAttachmentDTOs);
        postDTO.setPlatformPosts(platformPostDTOs);
        return postDTO;
    }

    private MediaType determineMediaType(String fileExtension) {
        if (fileExtension.equalsIgnoreCase(".jpg") || fileExtension.equalsIgnoreCase(".jpeg") ||
                fileExtension.equalsIgnoreCase(".png") || fileExtension.equalsIgnoreCase(".gif")) {
            return MediaType.IMAGE;
        } else if (fileExtension.equalsIgnoreCase(".mp4") || fileExtension.equalsIgnoreCase(".avi") ||
                fileExtension.equalsIgnoreCase(".mov")) {
            return MediaType.VIDEO;
        } else if (fileExtension.equalsIgnoreCase(".mp3") || fileExtension.equalsIgnoreCase(".wav")) {
            return MediaType.AUDIO;
        } else {
            return MediaType.DOCUMENT;
        }
    }
}