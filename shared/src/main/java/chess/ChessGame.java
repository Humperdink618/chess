package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn; // which team's turn it is
    private boolean isWhiteInCheck;
    private boolean isBlackInCheck;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        isWhiteInCheck = false;
        isBlackInCheck = false;
        // TODO set default board
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
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece chessPiece = board.getPiece(startPosition);
        Collection<ChessMove> moves = chessPiece.pieceMoves(board, startPosition);
        // can stop here if you want to just return moves without checking if in check and have makeMove() figure
        // that out OR you could figure out if this move would put you in check and remove the violating moves here
        // Filter these for check violations
        if(isInCheck(chessPiece.getTeamColor())) {
            // TODO: remove moves yay
        }
        // check to see if a move gets you in check
        //throw new RuntimeException("Not implemented");
        // may consider getBoard.clone()
        // clone board for each iteration, calling isInCheck() on each of them, and if none get me out of check,
        // it is checkmate
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //TODO make move board has been changed
        // check if move is in valid moves -> else throw
        //  update board
        // OR don't edit valid moves, and simply gaslight your players into thinking a move is
        // valid and then in this function tell them that you lied and throw an invalid move
        // exception if it puts them in check. Lie to them. Make them feel dumb. Feel empowered. >=)
        // after you execute your move, you check to see if the other team's king is in check.

        // TODO: likely will use this method to help implement the isInCheck, isInCheckmate, and isInStalemate
        //      methods. Iterate over the valid moves and create a temporary copy of the board using the .clone()
        //      method (scrapping it after each iteration), and make the move on that board, checking to see if
        //      that move would put your king in check, if your king is in check and if your move would get your king
        //      out of check or not, or if it is your turn or not. If it is an illegal move, throw the exception.
        //      Otherwise, execute the move on the original board.
        checkCalculator(TeamColor.WHITE);
        checkCalculator(TeamColor.BLACK);
        throw new RuntimeException("Not implemented");
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
    private void checkCalculator(TeamColor teamColor){
      //  this.isBlackInCheck = ?
      //  this.isWhiteInCheck = ?
        // TODO: use the board to check if the passed in team is in check and set it in the class.
        throw new RuntimeException("Not Implemented");
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
        // is in stalemate if no possible valid moves (validMoves() is empty) and !isInCheck()
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        checkCalculator(TeamColor.WHITE);
        checkCalculator(TeamColor.BLACK);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
