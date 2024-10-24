package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> moves = new HashSet<>();
        // Queen can move in 8 directions -> y+x+, y-x+, y-x- y-x+, y0x+, y+x0, y0x-, y-x0

        // y+x+
        addMoveLinear(board, myPosition, moves, 1, 1);
        // y+x-
        addMoveLinear(board, myPosition, moves, -1, 1);
        // y-x+
        addMoveLinear(board, myPosition, moves, 1, -1);
        // y-x-
        addMoveLinear(board, myPosition, moves, -1, -1);
        // y+x0
        addMoveLinear(board, myPosition, moves, 0, 1);
        // y0x+
        addMoveLinear(board, myPosition, moves, 1, 0);
        // y-x0
        addMoveLinear(board, myPosition, moves, 0, -1);
        // y0x-
        addMoveLinear(board, myPosition, moves, -1, 0);

        return moves;
    }
}