package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

        //throw new RuntimeException("Not implemented");
        return moves;



        //  return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
}
