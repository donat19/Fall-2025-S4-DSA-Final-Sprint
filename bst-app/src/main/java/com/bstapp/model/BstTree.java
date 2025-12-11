package com.bstapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * This is the JPA Entity class that represents a record in the database.
 * Each instance of BstTree corresponds to one row in the "bst_trees" table.
 * 
 * I'm using JPA (Java Persistence API) annotations to define how this class
 * maps to the database table. Spring Data JPA + Hibernate handle all the actual
 * SQL queries behind the scenes, which is really convenient.
 * 
 * The @Entity annotation marks this as a persistent entity, and @Table specifies
 * the table name in the database.
 */
@Entity
@Table(name = "bst_trees")
public class BstTree {
    
    /*
     * Primary key field. @Id marks it as the primary key.
     * @GeneratedValue with IDENTITY strategy means the database will auto-generate
     * the ID value (like AUTO_INCREMENT in MySQL or SERIAL in PostgreSQL).
     * H2 database handles this automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /*
     * This stores the original numbers that the user entered.
     * For example: "[7, 3, 9, 1, 4]"
     * I'm storing it as a String because it's just for display purposes.
     */
    @Column(name = "input_numbers", nullable = false)
    private String inputNumbers;
    
    /*
     * This stores the JSON representation of the BST.
     * I'm using TEXT column type because JSON can be pretty long,
     * especially for trees with many nodes, and VARCHAR might not be enough.
     */
    @Column(name = "tree_json", columnDefinition = "TEXT", nullable = false)
    private String treeJson;
    
    /*
     * Timestamp of when this tree was created.
     * Useful for sorting trees by date and showing the user when they created each tree.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /*
     * Default constructor required by JPA.
     * Hibernate needs this to create instances when loading from database.
     * I also set the createdAt timestamp here automatically.
     */
    public BstTree() {
        this.createdAt = LocalDateTime.now();
    }
    
    /*
     * Constructor for creating a new tree with the input numbers and JSON.
     * This is what I use in the service layer when saving a new tree.
     * The timestamp is automatically set to now.
     */
    public BstTree(String inputNumbers, String treeJson) {
        this.inputNumbers = inputNumbers;
        this.treeJson = treeJson;
        this.createdAt = LocalDateTime.now();
    }
    
    // ============ Getters and Setters ============
    // JPA needs these to access and modify the fields
    // Also needed for Jackson serialization when returning as JSON in API
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getInputNumbers() {
        return inputNumbers;
    }
    
    public void setInputNumbers(String inputNumbers) {
        this.inputNumbers = inputNumbers;
    }
    
    public String getTreeJson() {
        return treeJson;
    }
    
    public void setTreeJson(String treeJson) {
        this.treeJson = treeJson;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
