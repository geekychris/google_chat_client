package com.warp.googlechat.cli.commands;

import com.google.api.services.chat.v1.model.Message;
import com.google.api.services.chat.v1.model.Space;
import com.warp.googlechat.cli.service.GoogleChatService;
import com.warp.googlechat.cli.service.ServiceException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Shell commands for interacting with Google Chat.
 */
@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class GoogleChatCommands {

    private final GoogleChatService chatService;
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    /**
     * Lists all accessible spaces in Google Chat.
     *
     * @return A table of spaces
     */
    @ShellMethod(key = "list-spaces", value = "List all accessible Google Chat spaces")
    @ShellMethodAvailability("isAvailable")
    public Table listSpaces() {
        try {
            List<Space> spaces = chatService.listSpaces();
            
            if (spaces.isEmpty()) {
                return createSimpleTable("No spaces found or accessible");
            }
            
            String[][] data = new String[spaces.size() + 1][];
            data[0] = new String[]{"ID", "Name", "Type", "Created"};
            
            for (int i = 0; i < spaces.size(); i++) {
                Space space = spaces.get(i);
                data[i + 1] = new String[]{
                    chatService.getSpaceId(space),
                    chatService.getSpaceDisplayName(space),
                    space.getType(),
                    formatDateTime(space.getCreateTime())
                };
            }
            
            TableModel model = new ArrayTableModel(data);
            TableBuilder tableBuilder = new TableBuilder(model);
            return formatTable(tableBuilder);
        } catch (ServiceException e) {
            log.error("Error listing spaces", e);
            return createErrorTable("Error listing spaces: " + e.getMessage());
        }
    }

    /**
     * Finds a space by its display name.
     *
     * @param name The display name of the space to find
     * @return A table with the space details if found
     */
    @ShellMethod(key = "find-space", value = "Find a Google Chat space by name")
    @ShellMethodAvailability("isAvailable")
    public Table findSpace(
            @ShellOption(help = "Display name of the space to find") 
            @NotBlank(message = "Space name must not be blank") 
            String name) {
        try {
            Optional<Space> spaceOpt = chatService.findSpaceByName(name);
            
            if (spaceOpt.isEmpty()) {
                return createSimpleTable("No space found with name: " + name);
            }
            
            Space space = spaceOpt.get();
            String[][] data = new String[][] {
                {"ID", chatService.getSpaceId(space)},
                {"Name", chatService.getSpaceDisplayName(space)},
                {"Type", space.getType()},
                {"Created", formatDateTime(space.getCreateTime())}
            };
            
            TableModel model = new ArrayTableModel(data);
            TableBuilder tableBuilder = new TableBuilder(model);
            return formatTable(tableBuilder);
        } catch (ServiceException e) {
            log.error("Error finding space: {}", name, e);
            return createErrorTable("Error finding space: " + e.getMessage());
        }
    }

    /**
     * Lists recent messages from a space by ID.
     *
     * @param spaceId The ID of the space
     * @param limit Maximum number of messages to return (optional)
     * @return A table of messages
     */
    @ShellMethod(key = "list-messages", value = "List messages from a Google Chat space by ID")
    @ShellMethodAvailability("isAvailable")
    public Table listMessages(
            @ShellOption(help = "ID of the space to list messages from") 
            @NotBlank(message = "Space ID must not be blank") 
            String spaceId,
            
            @ShellOption(help = "Maximum number of messages to return", defaultValue = "10") 
            int limit) {
        try {
            List<Message> messages = chatService.getRecentMessages(spaceId, limit);
            
            if (messages.isEmpty()) {
                return createSimpleTable("No messages found in space: " + spaceId);
            }
            
            String[][] data = new String[messages.size() + 1][];
            data[0] = new String[]{"Time", "Sender", "Message"};
            
            for (int i = 0; i < messages.size(); i++) {
                Message message = messages.get(i);
                data[i + 1] = new String[]{
                    formatDateTime(message.getCreateTime()),
                    message.getSender() != null ? message.getSender().getName() : "Unknown",
                    message.getText() != null ? message.getText() : "(No text content)"
                };
            }
            
            TableModel model = new ArrayTableModel(data);
            TableBuilder tableBuilder = new TableBuilder(model);
            return formatTable(tableBuilder);
        } catch (ServiceException e) {
            log.error("Error listing messages", e);
            return createErrorTable("Error listing messages: " + e.getMessage());
        }
    }

    /**
     * Lists recent messages from a space by name.
     *
     * @param spaceName The name of the space
     * @param limit Maximum number of messages to return (optional)
     * @return A table of messages
     */
    @ShellMethod(key = "list-messages-by-name", value = "List messages from a Google Chat space by name")
    @ShellMethodAvailability("isAvailable")
    public Table listMessagesByName(
            @ShellOption(help = "Name of the space to list messages from") 
            @NotBlank(message = "Space name must not be blank") 
            String spaceName,
            
            @ShellOption(help = "Maximum number of messages to return", defaultValue = "10") 
            int limit) {
        try {
            List<Message> messages = chatService.getRecentMessagesBySpaceName(spaceName, limit);
            
            if (messages.isEmpty()) {
                return createSimpleTable("No messages found in space: " + spaceName);
            }
            
            String[][] data = new String[messages.size() + 1][];
            data[0] = new String[]{"Time", "Sender", "Message"};
            
            for (int i = 0; i < messages.size(); i++) {
                Message message = messages.get(i);
                data[i + 1] = new String[]{
                    formatDateTime(message.getCreateTime()),
                    message.getSender() != null ? message.getSender().getName() : "Unknown",
                    message.getText() != null ? message.getText() : "(No text content)"
                };
            }
            
            TableModel model = new ArrayTableModel(data);
            TableBuilder tableBuilder = new TableBuilder(model);
            return formatTable(tableBuilder);
        } catch (ServiceException e) {
            log.error("Error listing messages", e);
            return createErrorTable("Error listing messages: " + e.getMessage());
        }
    }

    /**
     * Gets the most recent message from a space.
     *
     * @param spaceName The name of the space
     * @return A table with the most recent message
     */
    @ShellMethod(key = "get-last-message", value = "Get the most recent message from a Google Chat space")
    @ShellMethodAvailability("isAvailable")
    public Table getLastMessage(
            @ShellOption(help = "Name of the space to get message from") 
            @NotBlank(message = "Space name must not be blank") 
            String spaceName) {
        try {
            List<Message> messages = chatService.getRecentMessagesBySpaceName(spaceName, 1);
            
            if (messages.isEmpty()) {
                return createSimpleTable("No messages found in space: " + spaceName);
            }
            
            Message message = messages.get(0);
            String[][] data = new String[][] {
                {"Time", formatDateTime(message.getCreateTime())},
                {"Sender", message.getSender() != null ? message.getSender().getName() : "Unknown"},
                {"Message", message.getText() != null ? message.getText() : "(No text content)"}
            };
            
            TableModel model = new ArrayTableModel(data);
            TableBuilder tableBuilder = new TableBuilder(model);
            return formatTable(tableBuilder);
        } catch (ServiceException e) {
            log.error("Error getting last message", e);
            return createErrorTable("Error getting last message: " + e.getMessage());
        }
    }

    /**
     * Sends a text message to a space by its ID.
     *
     * @param spaceId The ID of the space to send the message to
     * @param text The text content of the message
     * @return A confirmation message
     */
    @ShellMethod(key = "send-message", value = "Send a message to a Google Chat space by ID")
    @ShellMethodAvailability("isAvailable")
    public String sendMessage(
            @ShellOption(help = "ID of the space to send the message to") 
            @NotBlank(message = "Space ID must not be blank") 
            String spaceId,
            
            @ShellOption(help = "Text content of the message") 
            @NotBlank(message = "Message text must not be blank") 
            String text) {
        try {
            Message message = chatService.sendTextMessage(spaceId, text);
            return "Message sent successfully! Message ID: " + message.getName();
        } catch (ServiceException e) {
            log.error("Error sending message to space: {}", spaceId, e);
            return "Error sending message: " + e.getMessage();
        }
    }

    /**
     * Sends a text message to a space by its display name.
     *
     * @param spaceName The display name of the space
     * @param text The text content of the message
     * @return A confirmation message
     */
    @ShellMethod(key = "send-message-by-name", value = "Send a message to a Google Chat space by name")
    @ShellMethodAvailability("isAvailable")
    public String sendMessageByName(
            @ShellOption(help = "Display name of the space to send the message to") 
            @NotBlank(message = "Space name must not be blank") 
            String spaceName,
            
            @ShellOption(help = "Text content of the message") 
            @NotBlank(message = "Message text must not be blank") 
            String text) {
        try {
            Message message = chatService.sendTextMessageToSpaceByName(spaceName, text);
            return "Message sent successfully to space '" + spaceName + "'! Message ID: " + message.getName();
        } catch (ServiceException e) {
            log.error("Error sending message to space: {}", spaceName, e);
            return "Error sending message: " + e.getMessage();
        }
    }

    /**
     * Formats a table with consistent styling.
     *
     * @param tableBuilder The table builder to format
     * @return The formatted table
     */
    private Table formatTable(TableBuilder tableBuilder) {
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        return tableBuilder.build();
    }

    /**
     * Creates a simple table with a single message.
     *
     * @param message The message to display
     * @return A table with the message
     */
    private Table createSimpleTable(String message) {
        String[][] data = new String[][] {
            { message }
        };
        TableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model);
        return formatTable(tableBuilder);
    }

    /**
     * Creates an error table with a message.
     *
     * @param errorMessage The error message to display
     * @return A table with the error message
     */
    private Table createErrorTable(String errorMessage) {
        String[][] data = new String[][] {
            { "ERROR: " + errorMessage }
        };
        TableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model);
        return formatTable(tableBuilder);
    }

    /**
     * Formats a timestamp for display.
     *
     * @param timestamp The timestamp string to format
     * @return The formatted datetime string
     */
    private String formatDateTime(String timestamp) {
        if (timestamp == null) {
            return "N/A";
        }
        try {
            Instant instant = Instant.parse(timestamp);
            return DATE_FORMATTER.format(instant);
        } catch (Exception e) {
            return timestamp;
        }
    }

    /**
     * Checks if commands are available.
     *
     * @return Availability of the commands
     */
    public Availability isAvailable() {
        try {
            // Test connection by listing spaces
            chatService.listSpaces();
            return Availability.available();
        } catch (Exception e) {
            return Availability.unavailable("Google Chat connection unavailable: " + e.getMessage());
        }
    }
}
