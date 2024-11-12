import chess.*;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        // for testing purposes. may or may not delete this later
      /*  String serverUrl = "I'm a URL"; // DEFINITELY fix this later.
        ChessClient client = new ChessClient(serverUrl);
        client.run();
       */
    }
}