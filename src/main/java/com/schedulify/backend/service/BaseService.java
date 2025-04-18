package com.schedulify.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {

    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    Optional<T> findById(ID id);

    T save(T entity);

    List<T> saveAll(List<T> entities);

    T update(ID id, T entity);

    void deleteById(ID id);

    void delete(T entity);

    boolean existsById(ID id);

    long count();
}