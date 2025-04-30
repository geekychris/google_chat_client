package com.warp.googlechat.lib.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration properties for Google Chat API integration.
 * This class holds all necessary configuration parameters for connecting to
 * and interacting with the Google Chat API.
 */
@Data
@Builder
@Slf4j
public class GoogleChatConfig {

    /**
     * Default application name used for Google API requests.
     */
    public static final String DEFAULT_APPLICATION_NAME = "Google Chat API Java Client";

    /**
     * Default path to store authorization tokens.
     */
    public static final String DEFAULT_TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Default scopes required for Google Chat API access.
     */
    public static final List<String> DEFAULT_SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/chat.spaces",
            "https://www.googleapis.com/auth/chat.messages"
    );

    /**
     * Application name used for Google API requests.
     */
    @NotBlank(message = "Application name must not be blank")
    @Builder.Default
    private String applicationName = DEFAULT_APPLICATION_NAME;

    /**
     * Path to the directory where authorization tokens are stored.
     */
    @NotBlank(message = "Tokens directory path must not be blank")
    @Builder.Default
    private String tokensDirectoryPath = DEFAULT_TOKENS_DIRECTORY_PATH;

    /**
     * Path to the credentials file (credentials.json).
     */
    private String credentialsFilePath;

    /**
     * OAuth 2.0 scopes for Google Chat API.
     */
    @NotNull(message = "Scopes must not be null")
    @Builder.Default
    private List<String> scopes = DEFAULT_SCOPES;

    /**
     * Port for the local redirect server for OAuth authentication flow.
     */
    @Positive(message = "Port must be a positive number")
    @Builder.Default
    private int port = 8888;

    /**
     * Host for the local redirect server for OAuth authentication flow.
     */
    @NotBlank(message = "Host must not be blank")
    @Builder.Default
    private String host = "localhost";

    /**
     * Get the path to the credentials file.
     *
     * @return Path object representing the credentials file
     */
    public Path getCredentialsFile() {
        if (credentialsFilePath == null || credentialsFilePath.isEmpty()) {
            log.warn("Credentials file path not specified, using default 'credentials.json'");
            return Paths.get("credentials.json");
        }
        return Paths.get(credentialsFilePath);
    }

    /**
     * Get the path to the tokens directory.
     *
     * @return Path object representing the tokens directory
     */
    public Path getTokensDirectory() {
        return Paths.get(tokensDirectoryPath);
    }
}
