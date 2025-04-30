package com.warp.googlechat.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Spring Boot application class for Google Chat CLI.
 */
@SpringBootApplication
@EnableConfigurationProperties({GoogleChatCliProperties.class})
public class GoogleChatCliApplication {

    /**
     * Main method to start the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GoogleChatCliApplication.class, args);
    }
}

