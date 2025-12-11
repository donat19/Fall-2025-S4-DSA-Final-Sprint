package com.bstapp.service;

import com.bstapp.model.BstTree;
import com.bstapp.repository.BstTreeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/*
 * This is the service class where all the business logic lives.
 * I learned that in Spring architecture, you should separate concerns:
 * - Controllers handle HTTP requests/responses
 * - Services contain business logic
 * - Repositories talk to the database
 * 
 * The @Service annotation marks this as a Spring bean that can be injected elsewhere.
 * It's basically the same as @Component but more descriptive.
 */
@Service
public class BstService {
    
    // Repository for saving and retrieving trees from the H2 database
    private final BstTreeRepository repository;
    
    // ObjectMapper from Jackson library - it converts Java objects to JSON and vice versa
    // Spring Boot auto-configures this for us which is really nice
    private final ObjectMapper objectMapper;
    
    /*
     * Constructor with dependency injection.
     * Spring will automatically find the BstTreeRepository and ObjectMapper beans
     * and pass them to this constructor when creating the BstService.
     */
    @Autowired
    public BstService(BstTreeRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }
    
    /*
     * This is the main method that ties everything together.
     * It takes a list of numbers, builds a BST from them, converts it to JSON,
     * and saves both the original numbers and the tree structure to the database.
     * 
     * I had to think about what to store - just the tree? Just the numbers?
     * I decided to store both because then I can show the user what they entered
     * and what tree was built from it.
     */
    public String buildAndSaveTree(List<Integer> numbers) {
        // First check if the input is valid
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Numbers list cannot be empty");
        }
        
        // Step 1: Build the Binary Search Tree from the numbers
        BstNode root = buildBst(numbers);
        
        // Step 2: Convert the tree to JSON format so it can be displayed nicely
        String treeJson = convertToJson(root);
        
        // Step 3: Convert the numbers list to a string for database storage
        // toString() gives us something like "[7, 3, 9, 1, 4]" which is fine for display
        String inputNumbers = numbers.toString();
        
        // Step 4: Create a new entity and save it to the database
        BstTree bstTree = new BstTree(inputNumbers, treeJson);
        repository.save(bstTree);
        
        // Return the JSON so the frontend can display the tree
        return treeJson;
    }
    
    /*
     * This method builds the actual Binary Search Tree!
     * It's pretty straightforward - I just iterate through the numbers list
     * and insert each number one by one into the tree.
     * 
     * The order of insertion matters a lot for the shape of the tree.
     * For example, if you insert [1, 2, 3, 4, 5] you get a completely unbalanced
     * right-skewed tree (basically a linked list). But if you insert [3, 1, 4, 2, 5]
     * you get a more balanced tree.
     * 
     * I'm not implementing any balancing here (like AVL or Red-Black trees)
     * because that would be extra work and wasn't required for this assignment.
     */
    public BstNode buildBst(List<Integer> numbers) {
        // Handle edge cases - return null if input is null or empty
        if (numbers == null || numbers.isEmpty()) {
            return null;
        }
        
        // Start with an empty tree (null root)
        BstNode root = null;
        
        // Insert each number into the tree one by one
        // The insert method handles finding the right spot
        for (Integer num : numbers) {
            root = insert(root, num);
        }
        
        return root;
    }
    
    /*
     * Recursive method to insert a value into the BST.
     * This is the classic BST insertion algorithm I learned in Data Structures class.
     * 
     * The idea is simple:
     * - If we hit a null node, that's where the new value goes
     * - If the value is less than current node, go left
     * - If the value is greater than current node, go right
     * - If the value equals current node, we skip it (no duplicates)
     * 
     * I chose not to allow duplicates because it makes the tree simpler
     * and matches the standard BST definition where each value is unique.
     */
    public BstNode insert(BstNode node, int value) {
        // Base case: if we've reached a null position, create a new node here
        if (node == null) {
            return new BstNode(value);
        }
        
        // Recursive case: decide whether to go left or right
        if (value < node.getValue()) {
            // Value is smaller, so it belongs in the left subtree
            node.setLeft(insert(node.getLeft(), value));
        } else if (value > node.getValue()) {
            // Value is larger, so it belongs in the right subtree
            node.setRight(insert(node.getRight(), value));
        }
        // If value == node.getValue(), we just don't insert it (skip duplicates)
        
        // Return the (possibly modified) node
        return node;
    }
    
    /*
     * This method converts the BST to a JSON string using Jackson ObjectMapper.
     * Jackson is a really popular JSON library in Java, and Spring Boot includes it by default.
     * 
     * I'm using writerWithDefaultPrettyPrinter() to make the output nicely formatted
     * with indentation and newlines, which makes it easier to read when displayed.
     * 
     * The BstNode class has the @JsonInclude annotation to exclude null children,
     * so leaf nodes won't have "left": null, "right": null in the output.
     */
    public String convertToJson(BstNode root) {
        try {
            // writeValueAsString converts the Java object to a JSON string
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            // This shouldn't normally happen with simple objects, but just in case
            throw new RuntimeException("Error converting tree to JSON", e);
        }
    }
    
    /*
     * Simple method to get all saved trees from the database.
     * I'm using a custom query method that orders by createdAt descending,
     * so the most recent trees appear first in the list.
     * 
     * Spring Data JPA is amazing - I didn't have to write any SQL!
     * Just by naming the method findAllByOrderByCreatedAtDesc, Spring figures out
     * what query to run. This is called "query derivation" and it's super convenient.
     */
    public List<BstTree> getAllTrees() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
    
    /*
     * This method parses the user's input string into a list of integers.
     * Users can enter numbers separated by commas, spaces, or both.
     * For example: "7, 3, 9" or "7 3 9" or "7,3,9" all work.
     * 
     * I'm using Java Streams here because it makes the code more readable (in my opinion).
     * The regex [,\s]+ splits on any combination of commas and whitespace.
     * 
     * If the user enters invalid input like "abc" or empty string, we throw an exception
     * which gets caught in the controller and returned as an error response.
     */
    public List<Integer> parseNumbers(String input) {
        // Check for null or empty input first
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }
        
        // Split the string by commas and/or spaces, convert to integers
        return Arrays.stream(input.split("[,\\s]+"))  // Split by comma or whitespace
                .filter(s -> !s.isEmpty())             // Remove empty strings
                .map(String::trim)                      // Trim whitespace from each part
                .map(Integer::parseInt)                 // Convert to Integer (throws if invalid)
                .toList();                              // Collect to list
    }
}
