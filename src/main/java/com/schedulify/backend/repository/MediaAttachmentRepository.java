package com.schedulify.backend.repository;

import com.schedulify.backend.model.entity.MediaAttachment;
import com.schedulify.backend.model.entity.Post;
import com.schedulify.backend.model.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MediaAttachment entity.
 */
@Repository
public interface MediaAttachmentRepository extends JpaRepository<MediaAttachment, Long> {

    List<MediaAttachment> findByPost(Post post);

    List<MediaAttachment> findByPostAndMediaType(Post post, MediaType mediaType);

    void deleteByPost(Post post);
} 