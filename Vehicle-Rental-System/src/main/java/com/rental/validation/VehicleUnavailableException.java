package com.rental.exception;

/**
 * Custom business logic exception thrown when an operation attempts to lease, 
 * reserve, or lock a vehicle asset that is not currently in an 'Available' state.
 * * @author Pathum Srinath
 */
public class VehicleUnavailableException extends Exception {
    
    /**
     * Default constructor with a generic error state message.
     */
    public VehicleUnavailableException() {
        super("The requested vehicle asset is currently unavailable for lease.");
    }

    /**
     * Constructs an instance with a customized contextual error message.
     * @param message Detailed reason for the exception
     */
    public VehicleUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs an instance with a customized message and root cause.
     * @param message Detailed reason for the exception
     * @param cause The underlying exception cause (e.g., a specific SQL state)
     */
    public VehicleUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}