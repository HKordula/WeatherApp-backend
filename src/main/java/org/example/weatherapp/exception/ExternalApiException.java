package org.example.weatherapp.exception;

// Custom exception to handle errors related to external API calls.
public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message); // Pass the error message to the RuntimeException.
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause); // Pass the error message and cause to the RuntimeException.
    }
}
