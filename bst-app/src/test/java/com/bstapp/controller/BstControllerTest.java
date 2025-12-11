package com.bstapp.controller;

import com.bstapp.model.BstTree;
import com.bstapp.service.BstService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * These are integration tests for my BstController class.
 * 
 * I'm using @WebMvcTest which is a Spring Boot annotation that loads only the web layer,
 * not the entire application. This makes the tests run faster because we don't need
 * to start the database or other components.
 * 
 * MockMvc is a testing utility that lets me send fake HTTP requests to the controller
 * and check the responses. It's like Postman but in code!
 * 
 * @MockBean creates a mock version of BstService that I can control in my tests.
 * This way I can test the controller without needing the actual service logic.
 */
@WebMvcTest(BstController.class)
class BstControllerTest {
    
    // MockMvc lets me send fake HTTP requests and check responses
    @Autowired
    private MockMvc mockMvc;
    
    // This is a mock (fake) version of the service that I control in tests
    @MockBean
    private BstService bstService;
    
    // ObjectMapper for converting objects to JSON (used in some tests)
    @Autowired
    private ObjectMapper objectMapper;
    
    /*
     * TEST 1: Can we load the enter-numbers page?
     * 
     * This is a simple test that just checks if GET /enter-numbers returns
     * status 200 (OK) and uses the correct Thymeleaf template.
     */
    @Test
    void testEnterNumbersPageLoads() throws Exception {
        mockMvc.perform(get("/enter-numbers"))
                .andExpect(status().isOk())          // Should return HTTP 200
                .andExpect(view().name("enter-numbers"));  // Should use enter-numbers.html template
    }
    
    /*
     * TEST 2: Does /process-numbers work correctly?
     * 
     * This test simulates what happens when a user submits numbers.
     * I mock the service to return a specific response, then check that
     * the controller returns it correctly.
     */
    @Test
    void testProcessNumbersSuccess() throws Exception {
        String inputNumbers = "7, 3, 9";
        List<Integer> parsedNumbers = Arrays.asList(7, 3, 9);
        String expectedJson = "{\"value\":7,\"left\":{\"value\":3},\"right\":{\"value\":9}}";
        
        // Tell the mock service what to return when methods are called
        when(bstService.parseNumbers(inputNumbers)).thenReturn(parsedNumbers);
        when(bstService.buildAndSaveTree(parsedNumbers)).thenReturn(expectedJson);
        
        // Send a POST request with JSON body and check the response
        mockMvc.perform(post("/process-numbers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numbers\":\"7, 3, 9\"}"))
                .andExpect(status().isOk())  // Should return HTTP 200
                .andExpect(content().string(expectedJson));  // Should return the tree JSON
    }
    
    /*
     * TEST 3: Does the controller handle invalid input correctly?
     * 
     * If the user enters something like "abc" instead of numbers,
     * the service will throw NumberFormatException, and the controller
     * should return a 400 Bad Request error.
     */
    @Test
    void testProcessNumbersInvalidInput() throws Exception {
        // Make the mock service throw an exception
        when(bstService.parseNumbers(anyString()))
                .thenThrow(new NumberFormatException("Invalid number"));
        
        mockMvc.perform(post("/process-numbers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numbers\":\"abc\"}"))
                .andExpect(status().isBadRequest());  // Should return HTTP 400
    }
    
    /*
     * TEST 4: Does the previous-trees page load with tree data?
     * 
     * This test checks that GET /previous-trees returns the page
     * and includes the trees in the model for Thymeleaf to render.
     */
    @Test
    void testPreviousTreesPageLoads() throws Exception {
        // Create a fake tree to return from the mock service
        BstTree tree1 = new BstTree("[7, 3, 9]", "{\"value\":7}");
        tree1.setId(1L);
        tree1.setCreatedAt(LocalDateTime.now());
        
        when(bstService.getAllTrees()).thenReturn(Collections.singletonList(tree1));
        
        mockMvc.perform(get("/previous-trees"))
                .andExpect(status().isOk())
                .andExpect(view().name("previous-trees"))  // Uses the right template
                .andExpect(model().attributeExists("trees"));  // Model has trees attribute
    }
    
    /*
     * TEST 5: Does the root URL redirect to /enter-numbers?
     * 
     * When someone goes to just "localhost:8080", they should be
     * redirected to the main input page.
     */
    @Test
    void testHomeRedirectsToEnterNumbers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())  // Should be a redirect (3xx status)
                .andExpect(redirectedUrl("/enter-numbers"));  // Redirect to this URL
    }
    
    /*
     * TEST 6: Does the API endpoint return JSON?
     * 
     * The /api/trees endpoint should return all trees as JSON data,
     * not an HTML page. This is useful if someone wants to access
     * the data programmatically.
     */
    @Test
    void testApiTreesReturnsJson() throws Exception {
        BstTree tree = new BstTree("[1, 2, 3]", "{\"value\":1}");
        tree.setId(1L);
        tree.setCreatedAt(LocalDateTime.now());
        
        when(bstService.getAllTrees()).thenReturn(Collections.singletonList(tree));
        
        mockMvc.perform(get("/api/trees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));  // Should be JSON
    }
}
