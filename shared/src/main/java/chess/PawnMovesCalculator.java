package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PawnMovesCalculator extends PieceMovesCalculator {

    // note: things are a little different for Pawn

    // Pawn can only move forward (i.e. the column position remains the same, unless capturing)
    // if path is obstructed, Pawn cannot move.
    // Pawn can only capture on diagonals

    // Pawn only moves on space at a time (except on it's first ever move in the game, in which if a white Pawn is on col 2
    // or if a black Pawn is on col 7, it can either move one space or two (if not obstructed))

    // if a Pawn reaches the edge of the board, it can be promoted (will need to override promotePiece() for this case)
    // thankfully, since a Pawn never starts on the edge of the board (row wise), this part should be the same for white and black

    // COLOR MATTERS!! Black Pawns move down columns, white Pawns move up columns

    // Pawn has to promote upon reaching the end of the board, even when capturing!

    // Pawn may borrow some aspects of the previous piece moves, but overall it must utilize its own movement method.

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
        addMoveLinear(board, myPosition, moves, -1, 1);
        // y-x+
        addMoveLinear(board, myPosition, moves, 1, -1);
        // y-x-
        addMoveLinear(board, myPosition, moves, -1, -1);

        //throw new RuntimeException("Not implemented");
        return moves;



        //  return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
}
