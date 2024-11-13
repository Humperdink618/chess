import chess.*;
import exceptions.ResponseException;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) throws ResponseException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        // Note: this is just for testing. Delete Later.
        String myURL = "http://localhost:8080"; // placeholder. Fix later.
        ChessClient client = new ChessClient(myURL);
        client.run();
    }
}