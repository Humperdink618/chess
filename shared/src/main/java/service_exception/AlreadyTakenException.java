package service_exception;

/**
 * Indicates there was an error when a resource with the same attribute already exists,
 * and it's not possible to create another resource with the same definition
 */

public class AlreadyTakenException extends Exception {
    public AlreadyTakenException(String message) {
        super(message);
    }
}
