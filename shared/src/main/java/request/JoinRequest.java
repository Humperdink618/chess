package request;

public record JoinRequest(
            String authToken,
            String playerColor,
            int gameID) {
}
// note: header types will be handled / declared / created through the handler. Here we just take in a string
