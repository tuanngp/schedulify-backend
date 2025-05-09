package com.schedulify.backend.repository;

import com.schedulify.backend.model.entity.PlatformPost;
import com.schedulify.backend.model.entity.Post;
import com.schedulify.backend.model.entity.SocialAccount;
import com.schedulify.backend.model.enums.Platform;
import com.schedulify.backend.model.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PlatformPost entity.
 */
@Repository
public interface PlatformPostRepository extends JpaRepository<PlatformPost, Long> {

    List<PlatformPost> findByPost(Post post);

    List<PlatformPost> findByPostAndPlatform(Post post, Platform platform);

    List<PlatformPost> findBySocialAccount(SocialAccount socialAccount);

    List<PlatformPost> findByStatus(PostStatus status);

    Optional<PlatformPost> findByPostAndPlatformAndSocialAccount(
            Post post, Platform platform, SocialAccount socialAccount);

    void deleteByPost(Post post);
} 