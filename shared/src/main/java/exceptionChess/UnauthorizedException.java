package exceptionChess;

/**
 * Indicates there was an error when a user's credentials (i.e. username, password, or authToken) are invalid
 */
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }
}
