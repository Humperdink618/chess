package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class KnightMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {


        Collection<ChessMove> moves = new HashSet<>();
        // knight can move to 8 locations -> y+2x+, y+x+2, y+x-2 y-2x+, y+2x-, y-x+2, y-x-2, y-2x-

        // y+x+
        addMoveNoLoop(board, myPosition, moves, 1, 2);
        // y+x-
        addMoveNoLoop(board, myPosition, moves, 2, 1);
        // y-x+
        addMoveNoLoop(board, myPosition, moves, 1, -2);
        // y-x-
        addMoveNoLoop(board, myPosition, moves, -2, 1);
        //
        addMoveNoLoop(board, myPosition, moves, -1, 2);
        // y+x-
        addMoveNoLoop(board, myPosition, moves, 2, -1);
        // y-x+
        addMoveNoLoop(board, myPosition, moves, -1, -2);
        // y-x-
        addMoveNoLoop(board, myPosition, moves, -2, -1);

        //throw new RuntimeException("Not implemented");
        return moves;



        //  return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
}
