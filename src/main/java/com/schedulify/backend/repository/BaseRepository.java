package com.schedulify.backend.repository;

import com.schedulify.backend.model.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.isDeleted = false")
    List<T> findAll();

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.isDeleted = false")
    Page<T> findAll(Pageable pageable);

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1 AND e.isDeleted = false")
    Optional<T> findById(ID id);

    @Query("SELECT e FROM #{#entityName} e WHERE e.isDeleted = true")
    List<T> findAllDeleted();

    @Query("SELECT e FROM #{#entityName} e WHERE e.isDeleted = true")
    Page<T> findAllDeleted(Pageable pageable);

    @Override
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.isDeleted = false")
    long count();

    @Override
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = ?1 AND e.isDeleted = false")
    boolean existsById(ID id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = true WHERE e.id = ?1")
    void softDeleteById(ID id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = false WHERE e.id = ?1")
    void restoreById(ID id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = true WHERE e.id IN ?1")
    void softDeleteByIds(List<ID> ids);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = false WHERE e.id IN ?1")
    void restoreByIds(List<ID> ids);
}