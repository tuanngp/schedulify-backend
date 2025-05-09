package com.schedulify.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base entity class that provides common attributes and behavior for all entities in the system.
 * This class implements auditing capabilities to track creation and modification details,
 * as well as soft delete functionality.
 *
 * @author Schedulify Team
 * @version 1.0
 */
@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the entity.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Timestamp when the entity was created.
     * Automatically set by Spring Data JPA auditing.
     * Cannot be updated once set.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the entity was last modified.
     * Automatically updated by Spring Data JPA auditing.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Username or ID of the user who created the entity.
     * Automatically set by Spring Data JPA auditing.
     * Cannot be updated once set.
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    /**
     * Username or ID of the user who last modified the entity.
     * Automatically updated by Spring Data JPA auditing.
     */
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * Flag indicating if the entity has been soft deleted.
     * True means the entity is considered deleted but remains in the database.
     */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    /**
     * Callback method that is executed before the entity is persisted.
     * Sets initial timestamps for creation and modification.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Callback method that is executed before the entity is updated.
     * Updates the last modified timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}