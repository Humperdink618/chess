package request;

public record LogoutRequest(String authToken) {
}
// note: because Logout doesn't actually return anything, we don't need to create a LogoutResult class
// (handlers and endpoints in the Server class will check to see if it returns empty {}'s and handles any specific
// exceptions). Same goes with JoinResult and ClearResult.
