package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class BishopMovesCalculator extends PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // figure out the type of piece
        // call the specific function
        //if (getPieceType() == PieceType.BISHOP) {
        // Bishop bishop = new Bishop(pieceColor, getPieceType());
        // return bishop.pieceMoves(board, myPosition);
        //  }

        // TODO -> the function calling pieceMoves doesn't know the type.
        //      fix functionality
        //      next time when implementing it, consider not making these into sub classes
        // TODO Test your functions on the test moves (debug) to figure out if it is correct.
        //      remember, since the base logic is the same for all piece movement, testing one will
        //      work for the other types unless the other type has something extra

        // use this for reference

        Collection<ChessMove> moves = new HashSet<>();
        // bishop can move in 4 directions -> y+x+, y-x+, y-x- y-x+

        // y+x+
        addMoveLinear(board, myPosition, moves, 1, 1);
        // y+x-
        addMoveLinear(board, myPosition, moves, 1, -1);
        // y-x+
        addMoveLinear(board, myPosition, moves, -1, 1);
        // y-x-
        addMoveLinear(board, myPosition, moves, -1, -1);

        //throw new RuntimeException("Not implemented");
        return moves;



      //  return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
}
