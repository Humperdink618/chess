package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame  {

    private ChessBoard board; // current board
    private TeamColor teamTurn; // which team's turn it is
    private boolean isWhiteInCheck; // is white in check in current board
    private boolean isBlackInCheck; // is black in check in current board

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        isWhiteInCheck = false;
        isBlackInCheck = false;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition){
        ChessPiece chessPiece = board.getPiece(startPosition);
        Collection<ChessMove> moves = chessPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> movesValid = new HashSet<>();
        // Filter these for check violations

        for(ChessMove move : moves){
            ChessBoard board1 = getBoard().clone();

            // make a move on the cloned board
            makeMoveHelper(move,board1,chessPiece);

            //check if the move you just made puts you in check
            if(!checkCalculator(chessPiece.getTeamColor(), board1)) {
                // TODO: add to list of final moves
                movesValid.add(move);
            }
            /*
            b.	Loop over theoretically possible moves for each piece
            i.	For each possible move:
            ii.	Clone grid
            iii.	Execute make move method to make the move
            iv.	Test if making the move would put player in check
            vi.	If no opponent piece can move to your king, move is valid.
            Add possible move to “list” of possible moves; destroy cloned grid.

            if my team is currently in check (isInCheck()), by the rules of chess, I cannot
            make a move that wouldn't get me out of check

            if(isInCheck()) {
                add moves that get me out of check
            } else {
                add moves that keep me out of check
            }
```````````*/
        }

        // check to see if a move gets you in check
        //throw new RuntimeException("Not implemented");
        // may consider getBoard.clone()
        // clone board for each iteration, calling isInCheck() on each of them, and if none get me out of check,
        // it is checkmate
        return movesValid;
    }
    public void makeMoveHelper(ChessMove move, ChessBoard board1, ChessPiece piece) {
        if(move.getPromotionPiece() != null){
            board1.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            board1.addPiece(move.getEndPosition(), piece);
        }
        board1.removePiece(move.getStartPosition());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return isWhiteInCheck == chessGame.isWhiteInCheck && isBlackInCheck == chessGame.isBlackInCheck && Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, isWhiteInCheck, isBlackInCheck);
    }

    private TeamColor opponentTeam(TeamColor team){
        if(team == TeamColor.BLACK){
            return TeamColor.WHITE;
        }
        return TeamColor.BLACK;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // make move board has been changed
        // check if move is in valid moves -> else throw
        //  update board

        // TODO: likely will use this method to help implement the isInCheck, isInCheckmate, and isInStalemate
        //      methods. Iterate over the valid moves and create a temporary copy of the board using the .clone()
        //      method (scrapping it after each iteration), and make the move on that board, checking to see if
        //      that move would put your king in check, if your king is in check and if your move would get your king
        //      out of check or not, or if it is your turn or not. If it is an illegal move, throw the exception.
        //      Otherwise, execute the move on the original board.

        if(!validMoves(move.getStartPosition()).contains(move)){
            throw new InvalidMoveException("You can't make that move, Stupid!");
        }
        if(board.getPiece(move.getStartPosition()).getTeamColor() != teamTurn){
            throw new InvalidMoveException("Not your turn, Moron!");
        } else {
            makeMoveHelper(move, board, board.getPiece(move.getStartPosition()));
        }
        this.isWhiteInCheck = checkCalculator(TeamColor.WHITE, board);
        this.isBlackInCheck = checkCalculator(TeamColor.BLACK, board);
        setTeamTurn(opponentTeam(teamTurn));
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // set this up as if it were a getter method, and create a function for checkCalculator to check if your
        // king is in check
        if(teamColor == TeamColor.WHITE){
            return isWhiteInCheck;
        } else {
            return isBlackInCheck;
        }
    }
    private boolean checkCalculator(TeamColor teamColor, ChessBoard board1){
        // TODO: use the board to check if the passed in team is in check and set it in the class.

        /*
        v.	Loop over opponent pieces
        1.	For each opponent piece, test if piece can move to the spot where your king is
        2.	If No, continue to the next opponent piece
        3.	If Yes, exit loop (possible move is not valid); destroy cloned grid. Rether to Step II.b
        */


        return false;
       // throw new RuntimeException("Not Implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // TODO: figure out how I am going to implement this. Should I calculate this every time I call it
        //      or not (depending on how often I will call it). If I call it a lot, use the same method I used
        //      for isInCheck. Otherwise, calculate it in this function. Same thing for isInStalemate.
        // validMoves() is empty, and isInCheck == true
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // TODO: same drill as in isInCheckmate()
        throw new RuntimeException("Not implemented");
        // is in stalemate if no possible valid moves (validMoves() for all pieces in a specific team is empty) and !isInCheck()
    }

    // TODO: create a new helper method to check to see if all validMoves for a team is empty
    // return false if I find a single valid move


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        this.isWhiteInCheck = checkCalculator(TeamColor.WHITE, board);
        this.isBlackInCheck = checkCalculator(TeamColor.BLACK, board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                ", isWhiteInCheck=" + isWhiteInCheck +
                ", isBlackInCheck=" + isBlackInCheck +
                '}';
    }




}
