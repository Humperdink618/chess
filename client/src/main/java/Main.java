import chess.*;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        // Note: this is just for testing. Delete Later.
        String myURL = "myURL"; // placeholder. Fix later.
        ChessClient client = new ChessClient(myURL); // TODO: update myURL with an actual serverURL later
        client.run();
    }
}