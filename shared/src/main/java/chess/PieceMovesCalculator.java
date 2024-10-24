package chess;

import java.util.Collection;

public abstract class PieceMovesCalculator {

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    protected static boolean outOfBounds( ChessPosition currPos, ChessBoard board) {
        if(currPos.getRow() -1 >= board.getBoardSize()
                || currPos.getColumn() -1 >= board.getBoardSize()
                || currPos.getRow() <= 0
                || currPos.getColumn() <= 0) {
            return true;
        }
        return false;
    }

    // can use this for all pieces except Pawn, King, and Knight
    protected static void addMoveLinear(ChessBoard board,
                                        ChessPosition myPosition,
                                        Collection<ChessMove> moves,
                                        int x,
                                        int y) {
        ChessPosition currPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        Boolean continueSearch = true;
        while (continueSearch) {
            currPos = new ChessPosition(currPos.getRow() + x, currPos.getColumn() + y);
            //  check if out of bounds first
            if (outOfBounds(currPos, board)) {
                continueSearch = false;
                // out of bounds. Stop iterating. Invalid move.
            } else if (board.getPiece(currPos) != null) {
                if (board.getPiece(currPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    // end position = currPos;
                    // check color to see if you include it or not
                    continueSearch = false;
                    moves.add(new ChessMove(myPosition, currPos, null));
                    break;
                } else {
                    continueSearch = false;
                    // stop before you reach that position
                }
            }
            // create position and add to list only if adding it is valid
            if (continueSearch) {
                moves.add(new ChessMove(myPosition, currPos, null));
            }
        }
    }

    // can use this for King and Knight (moves only once)
    protected static void addSingleMove(ChessBoard board,
                                        ChessPosition myPosition,
                                        Collection<ChessMove> moves,
                                        int x,
                                        int y) {
        ChessPosition currPos = new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + y);
        // check if out of bounds first
        if (!outOfBounds(currPos, board)) {
            if (board.getPiece(currPos) != null) {
                if (board.getPiece(currPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    // end position = currPos;
                    // check color to see if you include it or not
                    moves.add(new ChessMove(myPosition, currPos, null));
                }
            } else {
                // create position and add to list only if adding it is valid
                moves.add(new ChessMove(myPosition, currPos, null));
            }
        }
    }
}
        // 1) Get your row and column (current position object)
        // 2) y+x+
        //  a) get new row col positions as new position object -> need to actually get it
        //  b) then board to see if there is a piece there
        //      board.getPiece(ChessPosition position)
        //      i) no more moves here -> stop iterating
        //      ii) check color to see if you include or just up to it
        //  c) then check to see if off board (edges)
        //      board.getBoardSize() -> this is max x and max y min x,y = 0
        //      consider putting this in chessPiece.java
        //      i) if so, stop iterating and don't include
        //  d) create position and add to list
        //  e) continue until you have to stop (update current position object)
        // 3) y-x+ -> reset current position object
        // etc. for each