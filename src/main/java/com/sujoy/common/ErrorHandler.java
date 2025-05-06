package com.sujoy.common;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Centralized error handling utility class to provide consistent
 * error handling and logging across the application.
 * 
 * @author sujoy
 */
public class ErrorHandler {
    private static final Logger LOGGER = LogManager.getLogger(ErrorHandler.class);
    
    /**
     * Log an exception with a custom message at ERROR level
     * 
     * @param message Custom message describing the context of the error
     * @param exception The exception to log
     */
    public static void logError(String message, Throwable exception) {
        LOGGER.error(message, exception);
    }
    
    /**
     * Log an exception with a custom message at WARNING level
     * 
     * @param message Custom message describing the context of the warning
     * @param exception The exception to log
     */
    public static void logWarning(String message, Throwable exception) {
        LOGGER.warn(message, exception);
    }
    
    /**
     * Log a warning message without an exception
     * 
     * @param message Custom message describing the context of the warning
     */
    public static void logWarning(String message) {
        LOGGER.warn(message);
    }
    
    /**
     * Log an exception with a custom message at INFO level
     * 
     * @param message Custom message describing the context of the info
     * @param exception The exception to log
     */
    public static void logInfo(String message, Throwable exception) {
        LOGGER.info(message, exception);
    }
    
    /**
     * Log an info message without an exception
     * 
     * @param message Custom message describing the context of the info
     */
    public static void logInfo(String message) {
        LOGGER.info(message);
    }
    
    /**
     * Log a message at DEBUG level
     * 
     * @param message Debug message
     */
    public static void logDebug(String message) {
        LOGGER.debug(message);
    }
    
    /**
     * Log an exception with a custom message at DEBUG level
     * 
     * @param message Debug message
     * @param exception The exception to log
     */
    public static void logDebug(String message, Throwable exception) {
        LOGGER.debug(message, exception);
    }
    
    /**
     * Get the stack trace of an exception as a string
     * 
     * @param exception The exception to get the stack trace from
     * @return The stack trace as a string
     */
    public static String getStackTraceAsString(Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Handle an exception by logging it and returning a default value
     * 
     * @param <T> The type of the default value
     * @param message Custom message describing the context of the error
     * @param exception The exception to handle
     * @param defaultValue The default value to return
     * @return The default value
     */
    public static <T> T handleException(String message, Throwable exception, T defaultValue) {
        logError(message, exception);
        return defaultValue;
    }
    
    /**
     * Handle an exception by logging it and rethrowing it as a RuntimeException
     * 
     * @param message Custom message describing the context of the error
     * @param exception The exception to handle
     * @throws RuntimeException Always thrown with the original exception as cause
     */
    public static void handleExceptionAndRethrow(String message, Throwable exception) {
        logError(message, exception);
        throw new RuntimeException(message, exception);
    }
}