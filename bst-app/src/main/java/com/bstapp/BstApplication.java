package com.bstapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * This is the main class of my BST application.
 * I spent a lot of time figuring out how Spring Boot works.
 * Basically this annotation @SpringBootApplication does a lot of magic behind the scenes,
 * it combines @Configuration, @EnableAutoConfiguration and @ComponentScan together.
 * Without this the app wouldn't start properly.
 */
@SpringBootApplication
public class BstApplication {
    
    /*
     * This is where the whole application starts running.
     * The main method is the entry point for any Java program.
     * SpringApplication.run() starts up the Spring context and the embedded Tomcat server.
     * I had to read the documentation to understand why we pass the class itself as argument.
     */
    public static void main(String[] args) {
        SpringApplication.run(BstApplication.class, args);
    }
}
