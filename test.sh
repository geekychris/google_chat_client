#!/bin/bash

echo "Google Chat CLI Test Script"
echo "-------------------------"

# Check if credentials file exists
if [ ! -f "credentials.json" ]; then
    echo "Error: credentials.json not found!"
    echo "Please download your OAuth 2.0 credentials from Google Cloud Console and save as credentials.json"
    exit 1
fi

# Run the application with help command to verify installation
echo "Testing CLI installation..."
java -jar googlechat-cli/target/googlechat-cli-0.0.1-SNAPSHOT-exec.jar help

echo ""
echo "Available commands:"
echo "- list-spaces: List all accessible Google Chat spaces"
echo "- find-space \"Space Name\": Find a space by name"
echo "- send-message <space-id> \"message\": Send a message to a space by ID"
echo "- send-message-by-name \"Space Name\" \"message\": Send a message to a space by name"
