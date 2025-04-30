package com.warp.googlechat.cli;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Configuration properties for Google Chat CLI.
 */
@ConfigurationProperties(prefix = "google.chat")
@Data
@Validated
public class GoogleChatCliProperties {

    /**
     * Application name used for Google API requests.
     */
    @NotBlank(message = "Application name must not be blank")
    private String applicationName = "Google Chat CLI";

    /**
     * Path to the credentials file (credentials.json).
     */
    private String credentialsFilePath = "credentials.json";

    /**
     * Path to the directory where authorization tokens are stored.
     */
    private String tokensDirectoryPath = "tokens";

    /**
     * OAuth 2.0 scopes for Google Chat API.
     */
    @NotNull(message = "Scopes must not be null")
    private List<String> scopes = List.of(
            "https://www.googleapis.com/auth/chat.spaces",
            "https://www.googleapis.com/auth/chat.messages"
    );

    /**
     * Port for the local redirect server for OAuth authentication flow.
     */
    private int port = 8888;

    /**
     * Host for the local redirect server for OAuth authentication flow.
     */
    private String host = "localhost";
}
