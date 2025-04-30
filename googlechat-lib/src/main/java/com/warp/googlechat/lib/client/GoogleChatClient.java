package com.warp.googlechat.lib.client;

import com.google.api.services.chat.v1.HangoutsChat;
import com.google.api.services.chat.v1.model.ListMessagesResponse;
import com.google.api.services.chat.v1.model.ListSpacesResponse;
import com.google.api.services.chat.v1.model.Message;
import com.google.api.services.chat.v1.model.Space;
import com.warp.googlechat.lib.auth.GoogleChatCredentials;
import com.warp.googlechat.lib.config.GoogleChatConfig;
import com.warp.googlechat.lib.exception.GoogleChatException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Client for interacting with Google Chat API.
 */
@Slf4j
public class GoogleChatClient {

    private final GoogleChatConfig config;
    private final GoogleChatCredentials credentials;
    private HangoutsChat chatService;

    /**
     * Creates a new GoogleChatClient with the specified configuration.
     *
     * @param config The Google Chat configuration
     */
    public GoogleChatClient(GoogleChatConfig config) {
        this.config = config;
        this.credentials = new GoogleChatCredentials(config);
        initialize();
    }

    /**
     * Initializes the Google Chat service.
     *
     * @throws GoogleChatException if initialization fails
     */
    private void initialize() throws GoogleChatException {
        try {
            log.info("Initializing Google Chat client with application name: {}", config.getApplicationName());
            
            chatService = new HangoutsChat.Builder(
                    credentials.createAuthorizedTransport(),
                    credentials.getJsonFactory(),
                    credentials.getCredentials(credentials.createAuthorizedTransport()))
                    .setApplicationName(config.getApplicationName())
                    .build();
                    
            log.info("Google Chat client initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Google Chat client", e);
            throw new GoogleChatException("Failed to initialize Google Chat client", e);
        }
    }

    /**
     * Lists all accessible spaces in Google Chat.
     *
     * @return List of spaces
     * @throws GoogleChatException if an error occurs
     */
    public List<Space> listSpaces() throws GoogleChatException {
        try {
            log.debug("Listing Google Chat spaces");
            ListSpacesResponse response = chatService.spaces().list().execute();
            List<Space> spaces = response.getSpaces();
            
            if (spaces == null) {
                log.info("No spaces found or accessible");
                return Collections.emptyList();
            }
            
            log.info("Found {} spaces", spaces.size());
            return spaces;
        } catch (IOException e) {
            log.error("Failed to list spaces", e);
            throw new GoogleChatException("Failed to list spaces", e);
        }
    }

    /**
     * Finds a space by its display name.
     *
     * @param spaceName The display name of the space to find
     * @return Optional containing the space if found, empty otherwise
     * @throws GoogleChatException if an error occurs
     */
    public Optional<Space> findSpaceByName(String spaceName) throws GoogleChatException {
        log.debug("Finding space with name: {}", spaceName);
        return listSpaces().stream()
                .filter(space -> space.getDisplayName() != null && 
                        space.getDisplayName().equalsIgnoreCase(spaceName))
                .findFirst();
    }

    /**
     * Sends a text message to the specified space.
     *
     * @param spaceId The ID of the space to send the message to
     * @param text The text content of the message
     * @return The created message
     * @throws GoogleChatException if an error occurs
     */
    public Message sendTextMessage(String spaceId, String text) throws GoogleChatException {
        try {
            log.debug("Sending message to space: {}", spaceId);
            Message message = new Message().setText(text);
            Message response = chatService.spaces().messages()
                    .create("spaces/" + spaceId, message)
                    .execute();
                    
            log.info("Message sent successfully to space: {}", spaceId);
            return response;
        } catch (IOException e) {
            log.error("Failed to send message to space: {}", spaceId, e);
            throw new GoogleChatException("Failed to send message", e);
        }
    }

    /**
     * Sends a text message to a space identified by its display name.
     *
     * @param spaceName The display name of the space
     * @param text The text content of the message
     * @return The created message
     * @throws GoogleChatException if the space is not found or an error occurs
     */
    public Message sendTextMessageToSpaceByName(String spaceName, String text) throws GoogleChatException {
        log.debug("Sending message to space with name: {}", spaceName);
        
        Space space = findSpaceByName(spaceName)
                .orElseThrow(() -> new GoogleChatException("Space not found: " + spaceName));
                
        String spaceId = space.getName().substring(space.getName().lastIndexOf('/') + 1);
        return sendTextMessage(spaceId, text);
    }

    /**
     * Lists messages from a space.
     *
     * @param spaceId The ID of the space
     * @param pageSize Maximum number of messages to return (default 100)
     * @param pageToken Token for the next page of messages
     * @return List of messages from the space
     * @throws GoogleChatException if an error occurs
     */
    public ListMessagesResponse listMessages(String spaceId, Integer pageSize, String pageToken) throws GoogleChatException {
        try {
            log.debug("Listing messages from space: {}", spaceId);
            
            String spaceName = spaceId.startsWith("spaces/") ? spaceId : "spaces/" + spaceId;
            HangoutsChat.Spaces.Messages.List request = chatService.spaces().messages().list(spaceName);
            
            if (pageSize != null) {
                request.setPageSize(pageSize);
            }
            if (pageToken != null) {
                request.setPageToken(pageToken);
            }
            
            ListMessagesResponse response = request.execute();
            List<Message> messages = response.getMessages();
            
            if (messages == null) {
                log.info("No messages found in space: {}", spaceId);
                response.setMessages(Collections.emptyList());
            } else {
                log.info("Found {} messages in space", messages.size());
            }
            
            return response;
        } catch (IOException e) {
            log.error("Failed to list messages from space: {}", spaceId, e);
            throw new GoogleChatException("Failed to list messages", e);
        }
    }

    /**
     * Lists messages from a space identified by its display name.
     *
     * @param spaceName The display name of the space
     * @param pageSize Maximum number of messages to return
     * @param pageToken Token for the next page of messages
     * @return List of messages from the space
     * @throws GoogleChatException if the space is not found or an error occurs
     */
    public ListMessagesResponse listMessagesBySpaceName(String spaceName, Integer pageSize, String pageToken) throws GoogleChatException {
        log.debug("Listing messages from space with name: {}", spaceName);
        
        Space space = findSpaceByName(spaceName)
                .orElseThrow(() -> new GoogleChatException("Space not found: " + spaceName));
                
        return listMessages(space.getName(), pageSize, pageToken);
    }

    /**
     * Gets the most recent messages from a space.
     *
     * @param spaceId The ID of the space
     * @param maxMessages Maximum number of messages to return
     * @return List of most recent messages
     * @throws GoogleChatException if an error occurs
     */
    public List<Message> getRecentMessages(String spaceId, int maxMessages) throws GoogleChatException {
        try {
            log.debug("Getting {} recent messages from space: {}", maxMessages, spaceId);
            
            ListMessagesResponse response = listMessages(spaceId, maxMessages, null);
            return response.getMessages() != null ? response.getMessages() : Collections.emptyList();
        } catch (GoogleChatException e) {
            log.error("Failed to get recent messages from space: {}", spaceId, e);
            throw new GoogleChatException("Failed to get recent messages", e);
        }
    }

    /**
     * Gets the most recent messages from a space identified by its display name.
     *
     * @param spaceName The display name of the space
     * @param maxMessages Maximum number of messages to return
     * @return List of most recent messages
     * @throws GoogleChatException if the space is not found or an error occurs
     */
    public List<Message> getRecentMessagesBySpaceName(String spaceName, int maxMessages) throws GoogleChatException {
        log.debug("Getting {} recent messages from space with name: {}", maxMessages, spaceName);
        
        Space space = findSpaceByName(spaceName)
                .orElseThrow(() -> new GoogleChatException("Space not found: " + spaceName));
                
        return getRecentMessages(space.getName(), maxMessages);
    }
}
