package com.schedulify.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Base service interface that provides standard CRUD operations and common functionality
 * for all services in the application.
 *
 * @param <T> the type of entity this service manages
 * @param <ID> the type of the entity's identifier
 *
 * @author Schedulify Team
 * @version 1.0
 */
public interface BaseService<T, ID> {

    /**
     * Retrieves all entities of type T.
     *
     * @return a list containing all entities
     */
    List<T> findAll();

    /**
     * Retrieves a page of entities using the provided pagination information.
     *
     * @param pageable pagination information
     * @return a page of entities
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Finds an entity by its identifier.
     *
     * @param id the identifier of the entity to find
     * @return an Optional containing the found entity, or empty if not found
     */
    Optional<T> findById(ID id);

    /**
     * Saves a new entity or updates an existing one.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);

    /**
     * Saves multiple entities in a batch operation.
     *
     * @param entities the list of entities to save
     * @return the list of saved entities
     */
    List<T> saveAll(List<T> entities);

    /**
     * Updates an existing entity identified by the given ID.
     *
     * @param id the identifier of the entity to update
     * @param entity the entity with updated data
     * @return the updated entity
     * @throws ResourceNotFoundException if the entity is not found
     */
    T update(ID id, T entity);

    /**
     * Soft deletes an entity by its identifier.
     * The entity is marked as deleted instead of being physically removed from the database.
     *
     * @param id the identifier of the entity to delete
     * @throws ResourceNotFoundException if the entity is not found
     */
    void deleteById(ID id);

    /**
     * Soft deletes the provided entity.
     * The entity is marked as deleted instead of being physically removed from the database.
     *
     * @param entity the entity to delete
     */
    void delete(T entity);

    /**
     * Checks if an entity with the given identifier exists.
     *
     * @param id the identifier to check
     * @return true if an entity exists with the given ID, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Counts the total number of non-deleted entities.
     *
     * @return the number of entities
     */
    long count();
}