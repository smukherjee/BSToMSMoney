package com.sujoy.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized error handling utility class to provide consistent
 * error handling and logging across the application.
 * 
 * @author sujoy
 */
public class ErrorHandler {
    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());
    
    /**
     * Log an exception with a custom message at ERROR level
     * 
     * @param message Custom message describing the context of the error
     * @param exception The exception to log
     */
    public static void logError(String message, Throwable exception) {
        LOGGER.log(Level.SEVERE, message, exception);
        System.err.println(message + ": " + exception.getMessage());
    }
    
    /**
     * Log an exception with a custom message at WARNING level
     * 
     * @param message Custom message describing the context of the warning
     * @param exception The exception to log
     */
    public static void logWarning(String message, Throwable exception) {
        LOGGER.log(Level.WARNING, message, exception);
        System.out.println("WARNING: " + message + ": " + exception.getMessage());
    }
    
    /**
     * Log an exception with a custom message at INFO level
     * 
     * @param message Custom message describing the context of the info
     * @param exception The exception to log
     */
    public static void logInfo(String message, Throwable exception) {
        LOGGER.log(Level.INFO, message, exception);
        System.out.println("INFO: " + message + ": " + exception.getMessage());
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