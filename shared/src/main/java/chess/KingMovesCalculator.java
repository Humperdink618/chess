package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class KingMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {


        Collection<ChessMove> moves = new HashSet<>();
        // King can move in 8 directions -> y+x+, y-x+, y-x- y-x+, y0x+, y+x0, y0x-, y-x0. However, King can only move one
        // square at a time

        // y+x+
        addMoveNoLoop(board, myPosition, moves, 1, 1);
        // y+x-
        addMoveNoLoop(board, myPosition, moves, -1, 1);
        // y-x+
        addMoveNoLoop(board, myPosition, moves, 1, -1);
        // y-x-
        addMoveNoLoop(board, myPosition, moves, -1, -1);
        // y+x0
        addMoveNoLoop(board, myPosition, moves, 0, 1);
        // y0x+
        addMoveNoLoop(board, myPosition, moves, 1, 0);
        // y-x0
        addMoveNoLoop(board, myPosition, moves, 0, -1);
        // y0x-
        addMoveNoLoop(board, myPosition, moves, -1, 0);

        //throw new RuntimeException("Not implemented");
        return moves;



        //  return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
}
