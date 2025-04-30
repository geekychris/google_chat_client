package com.warp.googlechat.lib.exception;

/**
 * Base exception for Google Chat API errors.
 */
public class GoogleChatException extends RuntimeException {

    /**
     * Constructs a new GoogleChatException with the specified detail message.
     *
     * @param message the detail message
     */
    public GoogleChatException(String message) {
        super(message);
    }

    /**
     * Constructs a new GoogleChatException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public GoogleChatException(String message, Throwable cause) {
        super(message, cause);
    }
}

