package com.schedulify.backend.service;

import com.schedulify.backend.exception.ResourceNotFoundException;
import com.schedulify.backend.model.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public List<T> saveAll(List<T> entities) {
        return repository.saveAll(entities);
    }

    @Override
    @Transactional
    public T update(ID id, T entity) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(entityClass.getSimpleName() + " not found with id: " + id);
        }
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        T entity = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + " not found with id: " + id));
        entity.setIsDeleted(true);
        repository.save(entity);
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