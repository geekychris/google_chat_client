package com.warp.googlechat.lib.exception;

/**
 * Exception thrown for Google Chat authentication errors.
 */
public class GoogleChatAuthException extends GoogleChatException {

    /**
     * Constructs a new GoogleChatAuthException with the specified detail message.
     *
     * @param message the detail message
     */
    public GoogleChatAuthException(String message) {
        super(message);
    }

    /**
     * Constructs a new GoogleChatAuthException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public GoogleChatAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

