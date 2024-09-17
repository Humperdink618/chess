package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class Bishop extends ChessPiece {
    public Bishop(ChessGame.TeamColor pieceColor, PieceType type) {
        super(pieceColor, type);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> moves = new HashSet<>();
        // bishop can move in 4 directions -> y+x+, y-x+, y-x- y-x+

        // y+x+
        addMoveLinear(board, myPosition, moves, 1, 1);
        // y+x-
        addMoveLinear(board, myPosition, moves, -1, 1);
        // y-x+
        addMoveLinear(board, myPosition, moves, 1, -1);
        // y-x-
        addMoveLinear(board, myPosition, moves, -1, -1);

        //throw new RuntimeException("Not implemented");
        return moves;
    }

}
