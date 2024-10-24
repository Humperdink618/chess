package exceptionChess;

/**
 * Indicates there was an error in the user's request
 */

public class BadRequestExceptionChess extends Exception {
    public BadRequestExceptionChess(String message) { super(message); }
}
