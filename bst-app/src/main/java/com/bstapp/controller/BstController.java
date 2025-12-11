package com.bstapp.controller;

import com.bstapp.model.BstTree;
import com.bstapp.service.BstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 * This is my controller class that handles all the HTTP requests for the BST application.
 * I learned that @Controller is used when you want to return HTML views with Thymeleaf.
 * If I used @RestController instead, it would return JSON by default which is not what I wanted
 * for the main pages (but I still needed it for the API endpoint).
 */
@Controller
public class BstController {
    
    // This is the service that does all the BST logic, I inject it here using dependency injection
    // which is a design pattern that Spring makes really easy to use
    private final BstService bstService;
    
    /*
     * Constructor injection - this is apparently the recommended way to do dependency injection in Spring.
     * I could also use @Autowired on the field directly but my professor said constructor injection is better
     * because it makes the dependencies explicit and easier to test.
     */
    @Autowired
    public BstController(BstService bstService) {
        this.bstService = bstService;
    }
    
    /*
     * This method handles GET requests to /enter-numbers URL.
     * It simply returns the name of the Thymeleaf template (enter-numbers.html)
     * which is located in src/main/resources/templates folder.
     * Spring automatically looks for it there which is pretty convenient.
     */
    @GetMapping("/enter-numbers")
    public String enterNumbers() {
        return "enter-numbers";
    }
    
    /*
     * When user goes to the root URL (just localhost:8080), I redirect them to the enter-numbers page.
     * The "redirect:" prefix tells Spring to send an HTTP redirect response (302 status code)
     * instead of looking for a template called "redirect:/enter-numbers".
     * This was confusing at first but now I understand how it works.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/enter-numbers";
    }
    
    /*
     * This is probably the most important method in the controller.
     * It handles the POST request when user submits numbers from the form.
     * 
     * @ResponseBody annotation is needed here because even though this is a @Controller,
     * I want this specific method to return JSON data, not an HTML view.
     * 
     * The @RequestBody annotation tells Spring to parse the incoming JSON and convert it
     * to a Map object. I'm using Map<String, String> because the JSON has a simple structure
     * like {"numbers": "7, 3, 9, 1, 4"}.
     * 
     * I wrapped everything in try-catch because many things can go wrong:
     * - User might enter letters instead of numbers (NumberFormatException)
     * - User might leave the field empty (IllegalArgumentException)
     * - Something unexpected might happen (general Exception)
     * 
     * ResponseEntity lets me return different HTTP status codes depending on what happened.
     */
    @PostMapping("/process-numbers")
    @ResponseBody
    public ResponseEntity<?> processNumbers(@RequestBody Map<String, String> payload) {
        try {
            // First I get the numbers string from the JSON payload
            String numbersInput = payload.get("numbers");
            
            // Then I parse it into a list of integers using my service
            List<Integer> numbers = bstService.parseNumbers(numbersInput);
            
            // Build the BST, save to database, and get JSON representation
            String treeJson = bstService.buildAndSaveTree(numbers);
            
            // Return 200 OK with the tree JSON
            return ResponseEntity.ok(treeJson);
        } catch (NumberFormatException e) {
            // User entered something that's not a number
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid number format. Please enter valid integers."));
        } catch (IllegalArgumentException e) {
            // User sent empty input or something similar
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Something else went wrong, return 500 error
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }
    
    /*
     * This method shows the page with all previously created trees.
     * The Model object is used to pass data from the controller to the Thymeleaf template.
     * I add the list of trees to the model with the name "trees", and then in the HTML
     * I can access it using th:each="tree : ${trees}".
     * It took me a while to understand how data flows from controller to view.
     */
    @GetMapping("/previous-trees")
    public String previousTrees(Model model) {
        // Get all trees from database through the service
        List<BstTree> trees = bstService.getAllTrees();
        
        // Add them to the model so Thymeleaf can access them
        model.addAttribute("trees", trees);
        
        // Return the template name
        return "previous-trees";
    }
    
    /*
     * This is a bonus REST API endpoint that returns all trees as pure JSON.
     * I added this because sometimes you might want to get the data programmatically
     * without loading the HTML page. Could be useful for testing or for a future
     * mobile app or something.
     * 
     * @ResponseBody makes it return JSON instead of looking for a template.
     */
    @GetMapping("/api/trees")
    @ResponseBody
    public ResponseEntity<List<BstTree>> getAllTreesApi() {
        return ResponseEntity.ok(bstService.getAllTrees());
    }
}
