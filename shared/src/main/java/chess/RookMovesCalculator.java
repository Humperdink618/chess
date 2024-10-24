package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> moves = new HashSet<>();
        // rook can move in 4 directions -> y+x0, y0x+, y-x0 y0x-

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