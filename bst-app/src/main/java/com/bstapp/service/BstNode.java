package com.bstapp.service;

import com.fasterxml.jackson.annotation.JsonInclude;

/*
 * This class represents a single node in the Binary Search Tree.
 * Each node has:
 * - A value (the number stored in this node)
 * - A left child (which will have a smaller value, or null if no left child)
 * - A right child (which will have a larger value, or null if no right child)
 * 
 * The @JsonInclude annotation is from the Jackson library. Setting it to NON_NULL means
 * that when this object is converted to JSON, fields that are null won't be included.
 * So a leaf node will just show {"value": 5} instead of {"value": 5, "left": null, "right": null}
 * which looks much cleaner!
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BstNode {
    
    // The integer value stored in this node
    private int value;
    
    // Reference to the left child node (values smaller than this node)
    private BstNode left;
    
    // Reference to the right child node (values greater than this node)
    private BstNode right;
    
    /*
     * Constructor that creates a new node with the given value.
     * When a node is first created, it doesn't have any children yet,
     * so left and right are both set to null.
     */
    public BstNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
    
    // ============ Getters and Setters ============
    // These are needed for Jackson to serialize the object to JSON
    // and also for the BST insertion logic to work.
    
    // Gets the value stored in this node
    public int getValue() {
        return value;
    }
    
    // Sets the value (not really used but included for completeness)
    public void setValue(int value) {
        this.value = value;
    }
    
    // Gets the left child
    public BstNode getLeft() {
        return left;
    }
    
    // Sets the left child - used when inserting a new node
    public void setLeft(BstNode left) {
        this.left = left;
    }
    
    // Gets the right child
    public BstNode getRight() {
        return right;
    }
    
    // Sets the right child - used when inserting a new node
    public void setRight(BstNode right) {
        this.right = right;
    }
}
