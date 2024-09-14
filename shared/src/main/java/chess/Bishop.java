package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Bishop extends ChessPiece {
    public Bishop(ChessGame.TeamColor pieceColor, PieceType type) {
        super(pieceColor, type);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // return new ArrayList<>();
        throw new RuntimeException("Not implemented");
    }
}
