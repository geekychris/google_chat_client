package com.warp.googlechat.cli.service;

import com.google.api.services.chat.v1.model.ListMessagesResponse;
import com.google.api.services.chat.v1.model.Message;
import com.google.api.services.chat.v1.model.Space;
import com.warp.googlechat.cli.GoogleChatCliProperties;
import com.warp.googlechat.lib.client.GoogleChatClient;
import com.warp.googlechat.lib.config.GoogleChatConfig;
import com.warp.googlechat.lib.exception.GoogleChatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for interacting with Google Chat API.
 */
@Service
@Slf4j
public class GoogleChatService {

    private final GoogleChatClient chatClient;

    /**
     * Creates a new GoogleChatService with the specified configuration.
     *
     * @param properties The CLI configuration properties
     */
    public GoogleChatService(GoogleChatCliProperties properties) {
        log.info("Initializing Google Chat service with application name: {}", properties.getApplicationName());
        
        GoogleChatConfig config = GoogleChatConfig.builder()
                .applicationName(properties.getApplicationName())
                .credentialsFilePath(properties.getCredentialsFilePath())
                .tokensDirectoryPath(properties.getTokensDirectoryPath())
                .scopes(properties.getScopes())
                .port(properties.getPort())
                .host(properties.getHost())
                .build();
                
        this.chatClient = new GoogleChatClient(config);
        log.info("Google Chat service initialized successfully");
    }

    /**
     * Lists all accessible spaces in Google Chat.
     *
     * @return List of spaces
     * @throws ServiceException if an error occurs
     */
    public List<Space> listSpaces() throws ServiceException {
        try {
            log.debug("Listing Google Chat spaces");
            return chatClient.listSpaces();
        } catch (GoogleChatException e) {
            log.error("Failed to list spaces", e);
            throw new ServiceException("Failed to list spaces: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a space by its display name.
     *
     * @param spaceName The display name of the space to find
     * @return Optional containing the space if found, empty otherwise
     * @throws ServiceException if an error occurs
     */
    public Optional<Space> findSpaceByName(String spaceName) throws ServiceException {
        try {
            log.debug("Finding space with name: {}", spaceName);
            return chatClient.findSpaceByName(spaceName);
        } catch (GoogleChatException e) {
            log.error("Failed to find space: {}", spaceName, e);
            throw new ServiceException("Failed to find space: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a text message to the specified space.
     *
     * @param spaceId The ID of the space to send the message to
     * @param text The text content of the message
     * @return The created message
     * @throws ServiceException if an error occurs
     */
    public Message sendTextMessage(String spaceId, String text) throws ServiceException {
        try {
            log.debug("Sending message to space: {}", spaceId);
            return chatClient.sendTextMessage(spaceId, text);
        } catch (GoogleChatException e) {
            log.error("Failed to send message to space: {}", spaceId, e);
            throw new ServiceException("Failed to send message: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a text message to a space identified by its display name.
     *
     * @param spaceName The display name of the space
     * @param text The text content of the message
     * @return The created message
     * @throws ServiceException if the space is not found or an error occurs
     */
    public Message sendTextMessageToSpaceByName(String spaceName, String text) throws ServiceException {
        try {
            log.debug("Sending message to space with name: {}", spaceName);
            return chatClient.sendTextMessageToSpaceByName(spaceName, text);
        } catch (GoogleChatException e) {
            log.error("Failed to send message to space: {}", spaceName, e);
            throw new ServiceException("Failed to send message: " + e.getMessage(), e);
        }
    }

    /**
     * Lists messages from a space.
     *
     * @param spaceId The ID of the space
     * @param pageSize Maximum number of messages to return
     * @param pageToken Token for the next page of messages
     * @return List of messages from the space
     * @throws ServiceException if an error occurs
     */
    public ListMessagesResponse listMessages(String spaceId, Integer pageSize, String pageToken) throws ServiceException {
        try {
            log.debug("Listing messages from space: {}", spaceId);
            return chatClient.listMessages(spaceId, pageSize, pageToken);
        } catch (GoogleChatException e) {
            log.error("Failed to list messages from space: {}", spaceId, e);
            throw new ServiceException("Failed to list messages: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the most recent messages from a space.
     *
     * @param spaceId The ID of the space
     * @param maxMessages Maximum number of messages to return
     * @return List of most recent messages
     * @throws ServiceException if an error occurs
     */
    public List<Message> getRecentMessages(String spaceId, int maxMessages) throws ServiceException {
        try {
            log.debug("Getting {} recent messages from space: {}", maxMessages, spaceId);
            return chatClient.getRecentMessages(spaceId, maxMessages);
        } catch (GoogleChatException e) {
            log.error("Failed to get recent messages from space: {}", spaceId, e);
            throw new ServiceException("Failed to get recent messages: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the most recent messages from a space by its display name.
     *
     * @param spaceName The display name of the space
     * @param maxMessages Maximum number of messages to return
     * @return List of most recent messages
     * @throws ServiceException if an error occurs
     */
    public List<Message> getRecentMessagesBySpaceName(String spaceName, int maxMessages) throws ServiceException {
        try {
            log.debug("Getting {} recent messages from space with name: {}", maxMessages, spaceName);
            return chatClient.getRecentMessagesBySpaceName(spaceName, maxMessages);
        } catch (GoogleChatException e) {
            log.error("Failed to get recent messages from space: {}", spaceName, e);
            throw new ServiceException("Failed to get recent messages: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the space ID from a space.
     * 
     * @param space The space object
     * @return The space ID
     */
    public String getSpaceId(Space space) {
        String name = space.getName();
        if (name.contains("/")) {
            return name.substring(name.lastIndexOf('/') + 1);
        }
        return name;
    }
    
    /**
     * Returns a formatted display name for a space.
     * 
     * @param space The space object
     * @return The formatted display name
     */
    public String getSpaceDisplayName(Space space) {
        return space.getDisplayName() != null ? space.getDisplayName() : "(Unnamed space)";
    }
}
