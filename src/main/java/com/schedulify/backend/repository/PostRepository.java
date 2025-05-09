package com.schedulify.backend.repository;

import com.schedulify.backend.model.entity.Post;
import com.schedulify.backend.model.entity.User;
import com.schedulify.backend.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Post entity.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthor(User author, Pageable pageable);

    Optional<Post> findByIdAndAuthor(Long id, User author);

    List<Post> findByStatus(PostStatus status);

    List<Post> findByStatusAndScheduledTimeLessThanEqual(PostStatus status, LocalDateTime now);
} 