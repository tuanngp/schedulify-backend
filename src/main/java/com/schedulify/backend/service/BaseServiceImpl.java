package com.schedulify.backend.service;

import com.schedulify.backend.exception.ResourceNotFoundException;
import com.schedulify.backend.model.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class BaseServiceImpl<T extends BaseEntity, ID> implements BaseService<T, ID> {

    protected final JpaRepository<T, ID> repository;
    protected final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        log.debug("Initializing {} with repository: {}", entityClass.getSimpleName(), repository.getClass().getSimpleName());
    }

    @Override
    public List<T> findAll() {
        log.debug("Finding all {} entities", entityClass.getSimpleName());
        List<T> result = repository.findAll();
        log.debug("Found {} {} entities", result.size(), entityClass.getSimpleName());
        return result;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<T> findById(ID id) {
        log.debug("Finding {} entity with id: {}", entityClass.getSimpleName(), id);
        Optional<T> result = repository.findById(id);
        log.debug("{} entity with id {}: {}",
            entityClass.getSimpleName(),
            id,
            result.isPresent() ? "found" : "not found"
        );
        return result;
    }

    @Override
    @Transactional
    public T save(T entity) {
        log.debug("Saving {} entity: {}", entityClass.getSimpleName(), entity);
        T saved = repository.save(entity);
        log.debug("Saved {} entity with id: {}", entityClass.getSimpleName(), saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public List<T> saveAll(List<T> entities) {
        return repository.saveAll(entities);
    }

    @Override
    @Transactional
    public T update(ID id, T entity) {
        log.debug("Updating {} entity with id: {}", entityClass.getSimpleName(), id);
        if (!repository.existsById(id)) {
            log.error("{} entity not found with id: {}", entityClass.getSimpleName(), id);
            throw new ResourceNotFoundException(entityClass.getSimpleName() + " not found with id: " + id);
        }
        T updated = repository.save(entity);
        log.debug("Updated {} entity with id: {}", entityClass.getSimpleName(), updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        log.debug("Soft deleting {} entity with id: {}", entityClass.getSimpleName(), id);
        T entity = findById(id)
                .orElseThrow(() -> {
                    log.error("{} entity not found with id: {}", entityClass.getSimpleName(), id);
                    return new ResourceNotFoundException(entityClass.getSimpleName() + " not found with id: " + id);
                });
        entity.setIsDeleted(true);
        repository.save(entity);
        log.debug("Soft deleted {} entity with id: {}", entityClass.getSimpleName(), id);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        entity.setIsDeleted(true);
        repository.save(entity);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }
}