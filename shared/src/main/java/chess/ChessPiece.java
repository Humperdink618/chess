package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return pieceColor;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        return type;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // figure out the type of piece
        // cal the specific function
        if (getPieceType() == PieceType.BISHOP) {
            Bishop bishop = new Bishop(pieceColor, getPieceType());
            return bishop.pieceMoves(board, myPosition);
        }
        // TODO -> the function calling piecemoves doesn't know the type.
        //      fix functionality
        //      next time when implementing it, consider not makeing these into sub classes
        // TODO Test your functions on the test moves (debug) to figure out if it is correct.
        //      remember, since the base logic is the same for all piece movement, testing one will
        //      work for the other types unless the other type has something extra

        return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
    // can use this for all pieces except Pawn
    protected static void addMoveLinear(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int x, int y) {
        ChessPosition currPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        Boolean continueSearch = true;
        while (continueSearch) {
            // TODO -> refactor method to create new function addMove() where only one move is in the function
            //      not including while loop -> add functionality to differnt types of pieces pawn weird
            // TODO -> extra refactor segments of addMove to work for pawn if want
            currPos = new ChessPosition(currPos.getRow() + x, currPos.getColumn() + y);
            // TODO check if out of bounds first
            if(board.getPiece(currPos) != null) {
                if(board.getPiece(currPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                    // end position = currPos;
                    // check color to see if you include it or not
                    continueSearch = false;
                    moves.add(new ChessMove(myPosition, currPos, null));
                    break;
                } else {
                    continueSearch = false;
                    // stop before you reach that position
                }
            } else if(currPos.getRow() == board.getBoardSize() || currPos.getColumn() == board.getBoardSize()
                    || currPos.getRow() == 0 || currPos.getColumn() == 0){
                continueSearch = false;

                // out of bounds. Stop iterating. Invalid move.
            }
            // create position and add to list only if adding it is valid
            if(continueSearch){
                moves.add(new ChessMove(myPosition, currPos, null));
            }
        }
        // 1) Get your row and column (current position object)
        // 2) y+x+
        //  a) get new row col postions as new postion object -> need to actually get it
        //  b) then board to see if there is a piece there
        //      board.getPiece(ChessPosition position)
        //      i) no more moves here -> stop iterating
        //      ii) check color to see if you include or just up to it
        //  c) then check to see if off board (edges)
        //      board.getBoardSize() -> this is max x and max y min x,y = 0
        //      consider putting this in chessPiece.java
        //      i) if so, stop iterating and don't include
        //  d) create position and add to list
        //  e) continue until you have to stop (update current positon object)
        // 3) y-x+ -> reset current position object
        // etc for each
    }
}
