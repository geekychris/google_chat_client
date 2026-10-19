<p align="center">
  <img src="assets/logo.png" alt="Google Chat Client logo" width="180">
</p>

# Google Chat Integration

A Java library and command-line interface for interacting with Google Chat. This project provides a reusable library for integrating with Google Chat API and a Spring Boot-based CLI application for quick access to common Google Chat operations.

## Project Structure

This project is organized as a multi-module Maven project:

- **googlechat-lib**: Core library for Google Chat API integration
- **googlechat-cli**: Command-line interface built with Spring Shell

## Setup Instructions

### Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- Google Cloud project with Chat API enabled
- OAuth 2.0 credentials

### Detailed Google Cloud Setup

1. **Create a Google Cloud Project**:
   - Go to [Google Cloud Console](https://console.cloud.google.com)
   - Click on "Select a Project" at the top
   - Click "New Project"
   - Enter a project name and click "Create"

2. **Enable the Google Chat API**:
   - In the Google Cloud Console, select your project
   - Go to "APIs & Services" > "Library"
   - Search for "Google Chat API"
   - Click on "Google Chat API"
   - Click "Enable"

3. **Configure OAuth Consent Screen**:
   - Go to "APIs & Services" > "OAuth consent screen"
   - Select "External" user type (unless you're in a Google Workspace organization)
   - Fill in the required fields:
     * App name: Your application name
     * User support email: Your email
     * Developer contact information: Your email
   - Click "Save and Continue"
   - Under "Scopes", add the following scopes:
     * `https://www.googleapis.com/auth/chat.spaces`
     * `https://www.googleapis.com/auth/chat.messages`
   - Click "Save and Continue"
   - Add test users (your Google account email)
   - Click "Save and Continue"

4. **Create OAuth 2.0 Credentials**:
   - Go to "APIs & Services" > "Credentials"
   - Click "Create Credentials" > "OAuth client ID"
   - Select "Desktop application" as the application type
   - Enter a name for your OAuth client
   - Click "Create"
   - Download the JSON file (click the download icon)
   - Save the downloaded file as `credentials.json` in your project directory

5. **Verify credentials.json Structure**:
   Your credentials.json should look similar to this (with your actual values):
   ```json
   {
     "installed": {
       "client_id": "your-client-id.apps.googleusercontent.com",
       "project_id": "your-project-id",
       "auth_uri": "https://accounts.google.com/o/oauth2/auth",
       "token_uri": "https://oauth2.googleapis.com/token",
       "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
       "client_secret": "your-client-secret",
       "redirect_uris": ["http://localhost"]
     }
   }
   ```

6. **First-Time Authentication**:
   - When you first run the application, it will open a browser window
   - Sign in with your Google account (must be added as a test user)
   - Grant the requested permissions
   - The application will store the tokens in the `tokens` directory

### Building the Project

```bash
cd googlechat-parent
mvn clean install
```

### Running the CLI

```bash
cd googlechat-cli
java -jar target/googlechat-cli-0.0.1-SNAPSHOT-exec.jar
```

## Usage Examples

Once the CLI is running, you can use the following commands:

### Space Management
```
google-chat:> list-spaces
google-chat:> find-space "My Team Chat"
```

### Sending Messages
```
google-chat:> send-message spaces/AAAAAAAAAAA "Hello from Google Chat CLI!"
google-chat:> send-message-by-name "My Team Chat" "Hello everyone!"
```

### Reading Messages
```
google-chat:> list-messages spaces/AAAAAAAAAAA 10
google-chat:> list-messages-by-name "My Team Chat" 10
google-chat:> get-last-message "My Team Chat"
```

### Get Command Help
```
google-chat:> help
```

## Configuration

You can customize the application by modifying the `application.yml` file or by providing environment variables or command-line arguments.

### Application Properties

| Property | Description | Default Value |
|----------|-------------|---------------|
| `google.chat.application-name` | Application name for Google API | Google Chat CLI |
| `google.chat.credentials-file-path` | Path to credentials JSON file | credentials.json |
| `google.chat.tokens-directory-path` | Directory to store OAuth tokens | tokens |
| `google.chat.port` | Port for OAuth redirect server | 8888 |
| `google.chat.host` | Host for OAuth redirect server | localhost |

### Using a Custom Configuration

```bash
java -jar googlechat-cli-0.0.1-SNAPSHOT-exec.jar --google.chat.credentials-file-path=/path/to/my-credentials.json
```

## Troubleshooting

### Common Issues

1. **Invalid Credentials**:
   - Verify your credentials.json is correctly formatted
   - Ensure you've enabled the Google Chat API
   - Check that your OAuth consent screen is configured
   - Verify you're listed as a test user

2. **Access Token Issues**:
   - Delete the `tokens` directory
   - Restart the application to trigger re-authentication

3. **Permission Errors**:
   - Verify the required scopes are enabled in the OAuth consent screen
   - Check that you've granted all requested permissions during authentication

4. **API Not Enabled**:
   - Go to Google Cloud Console
   - Navigate to "APIs & Services" > "Enabled APIs & services"
   - Verify "Google Chat API" is listed and enabled

## Library Usage

If you want to use the Google Chat library in your own Java application:

```java
// Create configuration
GoogleChatConfig config = GoogleChatConfig.builder()
        .applicationName("My Application")
        .credentialsFilePath("/path/to/credentials.json")
        .build();

// Initialize client
GoogleChatClient chatClient = new GoogleChatClient(config);

// List spaces
List<Space> spaces = chatClient.listSpaces();

// Send message
chatClient.sendTextMessageToSpaceByName("My Team Chat", "Hello from my Java application!");

// Read messages
List<Message> messages = chatClient.getRecentMessagesBySpaceName("My Team Chat", 10);
```

## Error Handling

The library and CLI handle common errors such as:
- Missing or invalid credentials
- Network connectivity issues
- Space not found
- Authorization failures

All errors are properly logged and include helpful error messages.

## Development

### Adding New Commands

To add new commands to the CLI:

1. Create a new method in `GoogleChatCommands.java`
2. Annotate with `@ShellMethod`
3. Add necessary parameters with `@ShellOption`
4. Implement the command logic using `GoogleChatService`

### Extending the Library

The library is designed to be extensible:

1. Core functionality is in `GoogleChatClient.java`
2. Configuration is managed through `GoogleChatConfig.java`
3. Authentication is handled by `GoogleChatCredentials.java`

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Acknowledgments

- Built using [Spring Shell](https://spring.io/projects/spring-shell)
- Uses [Google Chat API](https://developers.google.com/chat)
