package com.bstapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * These are my unit tests for the Binary Search Tree logic.
 * I tried to cover as many edge cases as I could think of.
 * 
 * Unit tests are important because they help me make sure my BST implementation
 * is correct before I try to use it in the actual application.
 * If something breaks later, these tests will help me find the problem.
 * 
 * I'm using JUnit 5 which is the testing framework that comes with Spring Boot.
 */
class BstServiceUnitTest {
    
    // The service I'm testing - contains all the BST logic
    private BstService bstService;
    
    /*
     * This method runs before each test.
     * I create a new instance of BstService with null dependencies because
     * I'm only testing the BST building logic, not the database stuff.
     * The repository and ObjectMapper are passed as null because they're not
     * needed for the pure BST logic tests.
     */
    @BeforeEach
    void setUp() {
        // Create service without dependencies for unit testing BST logic
        bstService = new BstService(null, null);
    }
    
    /*
     * TEST 1: Testing the basic BST construction
     * 
     * I'm inserting [7, 3, 9, 1, 4] and checking if the tree looks right.
     * The expected tree structure should be:
     *        7          <- root
     *       / \
     *      3   9        <- 3 goes left (3 < 7), 9 goes right (9 > 7)
     *     / \
     *    1   4          <- 1 goes left of 3, 4 goes right of 3
     * 
     * This is probably the most important test because it verifies the basic
     * BST insertion algorithm is working correctly.
     */
    @Test
    void testBstConstruction() {
        List<Integer> numbers = Arrays.asList(7, 3, 9, 1, 4);
        
        BstNode root = bstService.buildBst(numbers);
        
        // Check that we got a root node back
        assertNotNull(root);
        assertEquals(7, root.getValue());
        
        // Check the left subtree (should have 3)
        assertNotNull(root.getLeft());
        assertEquals(3, root.getLeft().getValue());
        
        // Check the right subtree (should have 9)
        assertNotNull(root.getRight());
        assertEquals(9, root.getRight().getValue());
        
        // Check that 1 is to the left of 3
        assertNotNull(root.getLeft().getLeft());
        assertEquals(1, root.getLeft().getLeft().getValue());
        
        // Check that 4 is to the right of 3
        assertNotNull(root.getLeft().getRight());
        assertEquals(4, root.getLeft().getRight().getValue());
        
        // Make sure the leaf nodes don't have any children
        assertNull(root.getLeft().getLeft().getLeft());
        assertNull(root.getLeft().getLeft().getRight());
        assertNull(root.getRight().getLeft());
        assertNull(root.getRight().getRight());
    }
    
    /*
     * TEST 2: Verifying the BST property is maintained
     * 
     * The BST property says that for every node:
     * - All values in the left subtree must be smaller than the node
     * - All values in the right subtree must be larger than the node
     * 
     * I'm using a helper method to recursively check this property for all nodes.
     * This test uses a different set of numbers to make sure it works generally.
     */
    @Test
    void testBstPropertyMaintained() {
        List<Integer> numbers = Arrays.asList(50, 30, 70, 20, 40, 60, 80);
        
        BstNode root = bstService.buildBst(numbers);
        
        // Use helper method to recursively verify BST property
        assertTrue(isBstValid(root, Integer.MIN_VALUE, Integer.MAX_VALUE));
    }
    
    /*
     * TEST 3: What happens with just one number?
     * 
     * If the user enters only one number, we should get a tree with just a root
     * and no children. This is an edge case that should work correctly.
     */
    @Test
    void testSingleElementTree() {
        List<Integer> numbers = Arrays.asList(42);
        
        BstNode root = bstService.buildBst(numbers);
        
        // Should have a root with the value 42
        assertNotNull(root);
        assertEquals(42, root.getValue());
        
        // But no children
        assertNull(root.getLeft());
        assertNull(root.getRight());
    }
    
    /*
     * TEST 4: What happens with an empty list?
     * 
     * If someone passes an empty list, we should return null
     * because there's no tree to build. This shouldn't crash!
     */
    @Test
    void testEmptyListReturnsNull() {
        List<Integer> numbers = Arrays.asList();
        
        BstNode root = bstService.buildBst(numbers);
        
        // Empty input should give null (no tree)
        assertNull(root);
    }
    
    /*
     * TEST 5: What happens with null input?
     * 
     * Similarly, passing null should also return null without crashing.
     * Defensive programming is important!
     */
    @Test
    void testNullListReturnsNull() {
        BstNode root = bstService.buildBst(null);
        
        // Null input should give null output
        assertNull(root);
    }
    
    /*
     * TEST 6: Do duplicates get inserted?
     * 
     * In my implementation, I chose not to insert duplicate values.
     * If you try to insert a number that's already in the tree, it just gets skipped.
     * This test verifies that behavior.
     * 
     * Input: [5, 3, 5, 7, 3, 5] should result in a tree with only [5, 3, 7]
     */
    @Test
    void testDuplicatesNotInserted() {
        List<Integer> numbers = Arrays.asList(5, 3, 5, 7, 3, 5);
        
        BstNode root = bstService.buildBst(numbers);
        
        // Tree should be: 5 with left child 3 and right child 7
        assertEquals(5, root.getValue());
        assertEquals(3, root.getLeft().getValue());
        assertEquals(7, root.getRight().getValue());
        
        // 3 should be a leaf (no children) since duplicates weren't inserted
        assertNull(root.getLeft().getLeft());
        assertNull(root.getLeft().getRight());
    }
    
    /*
     * TEST 7: What happens with ascending order input?
     * 
     * If you insert numbers in ascending order (1, 2, 3, 4, 5), you get a
     * degenerate tree that's basically a linked list going right.
     * This is a worst-case scenario for BST performance, but it should still
     * work correctly. This is why balanced trees like AVL exist!
     */
    @Test
    void testAscendingOrderCreatesRightSkewedTree() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        BstNode root = bstService.buildBst(numbers);
        
        // First element becomes root
        assertEquals(1, root.getValue());
        
        // Everything else goes to the right
        assertNull(root.getLeft());  // No left children at all
        assertEquals(2, root.getRight().getValue());
        assertEquals(3, root.getRight().getRight().getValue());
        // And so on...
    }
    
    /*
     * TEST 8: Basic number parsing test
     * 
     * The parseNumbers method takes a string like "7, 3, 9, 1, 4" and
     * converts it to a list of integers [7, 3, 9, 1, 4].
     * This is what happens when the user submits the form.
     */
    @Test
    void testNumberParsing() {
        String input = "7, 3, 9, 1, 4";
        
        List<Integer> numbers = bstService.parseNumbers(input);
        
        // Should get 5 numbers in the correct order
        assertEquals(5, numbers.size());
        assertEquals(Arrays.asList(7, 3, 9, 1, 4), numbers);
    }
    
    /*
     * TEST 9: Number parsing with just spaces (no commas)
     * 
     * I wanted to be flexible with input format, so users can also
     * enter numbers separated by just spaces like "10 20 30 40".
     * The regex in parseNumbers handles both commas and spaces.
     */
    @Test
    void testNumberParsingWithSpaces() {
        String input = "10 20 30 40";
        
        List<Integer> numbers = bstService.parseNumbers(input);
        
        assertEquals(4, numbers.size());
        assertEquals(Arrays.asList(10, 20, 30, 40), numbers);
    }
    
    /*
     * TEST 10: Invalid input should throw an exception
     * 
     * If someone enters "1, 2, abc, 4", we can't parse "abc" as an integer.
     * The method should throw NumberFormatException so the controller can
     * return an error message to the user.
     */
    @Test
    void testInvalidNumberParsingThrowsException() {
        String input = "1, 2, abc, 4";
        
        // assertThrows checks that the code inside the lambda throws the expected exception
        assertThrows(NumberFormatException.class, () -> {
            bstService.parseNumbers(input);
        });
    }
    
    /*
     * Helper method that recursively validates the BST property.
     * 
     * For each node, we check that:
     * - The value is greater than min (everything to the left of ancestors)
     * - The value is less than max (everything to the right of ancestors)
     * 
     * We pass down updated min/max values as we traverse:
     * - Going left: update max to current node's value
     * - Going right: update min to current node's value
     * 
     * This is a classic interview question algorithm that I learned in class!
     */
    private boolean isBstValid(BstNode node, int min, int max) {
        // Base case: empty trees are valid BSTs
        if (node == null) {
            return true;
        }
        
        // Check if current node violates the BST property
        if (node.getValue() <= min || node.getValue() >= max) {
            return false;  // Node is out of valid range
        }
        
        // Recursively check left and right subtrees with updated bounds
        return isBstValid(node.getLeft(), min, node.getValue()) &&
               isBstValid(node.getRight(), node.getValue(), max);
    }
}
