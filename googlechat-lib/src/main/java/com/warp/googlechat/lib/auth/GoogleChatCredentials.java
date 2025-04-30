package com.warp.googlechat.lib.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.warp.googlechat.lib.config.GoogleChatConfig;
import com.warp.googlechat.lib.exception.GoogleChatAuthException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * Handler for Google Chat API credentials and authentication.
 */
@Slf4j
public class GoogleChatCredentials {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final GoogleChatConfig config;

    /**
     * Create a new GoogleChatCredentials instance with the provided configuration.
     *
     * @param config The Google Chat configuration
     */
    public GoogleChatCredentials(GoogleChatConfig config) {
        this.config = config;
    }

    /**
     * Creates an authorized HTTP transport with OAuth 2.0 credentials.
     *
     * @return An authorized HTTP transport
     * @throws GoogleChatAuthException if an error occurs during authentication
     */
    public NetHttpTransport createAuthorizedTransport() throws GoogleChatAuthException {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = getCredentials(httpTransport);
            return httpTransport;
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to create authorized transport", e);
            throw new GoogleChatAuthException("Failed to create authorized transport", e);
        }
    }

    /**
     * Gets OAuth 2.0 credentials for the application.
     *
     * @param httpTransport The network HTTP transport
     * @return An OAuth 2.0 credential object
     * @throws IOException if an error occurs reading credentials
     * @throws FileNotFoundException if credentials file is not found
     */
    public Credential getCredentials(final NetHttpTransport httpTransport)
            throws IOException {
        
        log.debug("Loading credentials from: {}", config.getCredentialsFile());
        
        // Load client secrets
        GoogleClientSecrets clientSecrets;
        try (InputStreamReader in = new InputStreamReader(
                new FileInputStream(config.getCredentialsFile().toFile()), StandardCharsets.UTF_8)) {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, in);
        } catch (FileNotFoundException e) {
            log.error("Credentials file not found: {}", config.getCredentialsFile(), e);
            throw new FileNotFoundException("Credentials file not found: " + config.getCredentialsFile());
        }

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, config.getScopes())
                .setDataStoreFactory(new FileDataStoreFactory(config.getTokensDirectory().toFile()))
                .setAccessType("offline")
                .build();
                
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(config.getPort())
                .setHost(config.getHost())
                .build();
                
        log.info("Starting authorization flow for Google Chat API");
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    
    /**
     * Returns the JSON factory used for this client.
     * 
     * @return The JSON factory instance
     */
    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }
}

