package com.bstapp.repository;

import com.bstapp.model.BstTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * This is the repository interface for database operations on BstTree entities.
 * 
 * By extending JpaRepository, I get a ton of methods for free without writing any code:
 * - save(entity) - saves a new entity or updates existing one
 * - findById(id) - finds an entity by its primary key
 * - findAll() - gets all entities
 * - delete(entity) - deletes an entity
 * - count() - counts total entities
 * - and many more!
 * 
 * Spring Data JPA automatically creates an implementation of this interface at runtime.
 * I don't need to write any SQL or implementation code, which is pretty amazing.
 * 
 * The @Repository annotation marks this as a Spring Data repository.
 * It's not strictly necessary since JpaRepository is already detected, but it's good practice.
 */
@Repository
public interface BstTreeRepository extends JpaRepository<BstTree, Long> {
    
    /*
     * Custom query method to find all trees ordered by creation date (newest first).
     * 
     * This is called "query derivation" - Spring Data JPA parses the method name and
     * automatically generates the right SQL query. The method name breaks down as:
     * - findAll = SELECT * FROM bst_trees
     * - ByOrderBy = ORDER BY
     * - CreatedAt = created_at column
     * - Desc = DESC (descending)
     * 
     * So the generated SQL is something like:
     * SELECT * FROM bst_trees ORDER BY created_at DESC
     * 
     * I think this is one of the coolest features of Spring Data JPA!
     */
    List<BstTree> findAllByOrderByCreatedAtDesc();
}
