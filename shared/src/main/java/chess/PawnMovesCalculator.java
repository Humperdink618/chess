package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMovesCalculator extends PieceMovesCalculator {

/*
     note: things are a little different for Pawn

     Pawn can only move forward (i.e. the column position remains the same, unless capturing)

     if path is obstructed, Pawn cannot move.

     Pawn can only capture on diagonals
    */

    /*
     Pawn only moves on space at a time (except on it's first ever move in the game,
        in which if a white Pawn is on col 2 or if a black Pawn is on col 7,
        it can either move one space or two (if not obstructed))
     */

    /*
     if a Pawn reaches the edge of the board, it can be promoted (will need to override promotePiece() for this case)

     thankfully, since a Pawn never starts on the edge of the board (row wise),
     this part should be the same for white and black

     COLOR MATTERS!! Black Pawns move down columns, white Pawns move up columns

     Pawn has to promote upon reaching the end of the board, even when capturing!

     Pawn may borrow some aspects of the previous piece moves, but overall it must utilize its own movement method.

 */

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> moves = new HashSet<>();
        // check color to determine direction
        if(colorIsWhite(board, myPosition)){
            /*
            white Pawn moves

            if(moveTwiceWhite(myPosition, board)){
                addMovePawn(board, myPosition, moves, 1,1);
            }
             */
            addMovePawn(board, myPosition, moves, 1,2);
        } else {
            // black Pawn moves
            addMovePawn(board, myPosition, moves, -1, -2);
        }
        return moves;
    }

    private boolean canCapture(ChessBoard board, ChessPosition myPosition, ChessPosition newPos) {
        return board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor();
    }

    private boolean colorIsWhite(ChessBoard board, ChessPosition newPos) {
        return board.getPiece(newPos).getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    private boolean canPromote(ChessPosition currPos) {
        return currPos.getRow() == 8 || currPos.getRow() == 1;
    }

    private boolean moveTwiceWhite(ChessPosition myPosition,
                                   ChessPosition forward,
                                   ChessPosition forward2,
                                   ChessBoard board) {
        // mypos row == 2, 1st move in bounds && no piece, 2nd move in bounds && no piece
        return myPosition.getRow() == 2 && !outOfBounds(forward,board) && board.getPiece(forward) == null
                && !outOfBounds(forward2,board) && board.getPiece(forward2) == null;
    }

    private boolean moveTwiceBlack(ChessPosition myPosition,
                                   ChessPosition forward,
                                   ChessPosition forward2,
                                   ChessBoard board) {
        return myPosition.getRow() == 7 && !outOfBounds(forward,board) && board.getPiece(forward) == null
                && !outOfBounds(forward2,board) && board.getPiece(forward2) == null;
    }
    // set from my position set at move forward -> repeat move forward

    private void addMovePawn(ChessBoard board,
                             ChessPosition myPosition,
                             Collection<ChessMove> moves,
                             int forwardOneRow,
                             int forwardTwoRow) {
        ChessPosition forward = new ChessPosition(myPosition.getRow() + forwardOneRow, myPosition.getColumn());
        ChessPosition forward2 = new ChessPosition(myPosition.getRow() + forwardTwoRow, myPosition.getColumn());
        ChessPosition leftPos =
                new ChessPosition(myPosition.getRow() + forwardOneRow, myPosition.getColumn() -1);
        ChessPosition rightPos =
                new ChessPosition(myPosition.getRow() +forwardOneRow, myPosition.getColumn() +1);

        // Check forward
        moveForwardPawn(board, myPosition, moves, forward);
        if(moveTwiceWhite(myPosition, forward, forward2, board) || moveTwiceBlack(myPosition,forward,forward2, board)){
            moveForwardPawn(board,myPosition,moves,forward2);
        }
        moveDiagonal(board, myPosition, moves, leftPos);
        moveDiagonal(board, myPosition, moves, rightPos);
    }

    private void moveForwardPawn(ChessBoard board,
                                 ChessPosition myPosition,
                                 Collection<ChessMove> moves,
                                 ChessPosition forward) {
        if (!outOfBounds(forward, board)) {
            if (board.getPiece(forward) == null) {
                // one else statement only -> separates promotion and non promotion
                addMoveDiagonalPawn(myPosition, moves, forward);
            }
        }
    }

    private void moveDiagonal(ChessBoard board,
                              ChessPosition myPosition,
                              Collection<ChessMove> moves,
                              ChessPosition newPos) {
        if(!outOfBounds(newPos, board)){
            if(board.getPiece(newPos) != null){
                if(canCapture(board, myPosition, newPos)){
                    addMoveDiagonalPawn(myPosition, moves, newPos);
                }
            }
        }
    }

    private void addMoveDiagonalPawn(ChessPosition myPosition, Collection<ChessMove> moves, ChessPosition newPos) {
        if (canPromote(newPos)) {
            addPromotion(myPosition, moves, newPos);
        } else {
            moves.add(new ChessMove(myPosition, newPos, null));
        }
    }

    private static void addPromotion(ChessPosition myPosition, Collection<ChessMove> moves, ChessPosition newPos) {
        // moves.add(new ChessMove(myPosition, currPos, ChessPiece.PieceType.KING)); // ANARCHY!
        // moves.add(new ChessMove(myPosition, currPos, ChessPiece.PieceType.PAWN)); // ANARCHY!
        moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.KNIGHT));
        // the top two comments were just a joke. SERIOUSLY DO NOT PROMOTE PAWN TO THESE PIECES!
    }
    // can use this for King and Knight (moves only once)
}
