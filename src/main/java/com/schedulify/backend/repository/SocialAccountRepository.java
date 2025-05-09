package com.schedulify.backend.repository;

import com.schedulify.backend.model.entity.SocialAccount;
import com.schedulify.backend.model.entity.User;
import com.schedulify.backend.model.enums.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for social media accounts.
 */
@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    List<SocialAccount> findByUserAndActiveTrue(User user);

    Optional<SocialAccount> findByIdAndUser(Long id, User user);

    Optional<SocialAccount> findByPlatformAndAccountId(Platform platform, String accountId);

    List<SocialAccount> findByPlatformAndActiveTrue(Platform platform);
} 