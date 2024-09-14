package chess;

import java.util.Collection;

public class Knight extends ChessPiece {
    public Knight(ChessGame.TeamColor pieceColor, PieceType type) {
        super(pieceColor, type);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // return new ArrayList<>();
        throw new RuntimeException("Not implemented");
    }
}
