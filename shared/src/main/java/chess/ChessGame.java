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
    private boolean isGameOver; // checks if player has resigned.

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        isWhiteInCheck = false;
        isBlackInCheck = false;
        isGameOver = false;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
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
        for(ChessMove move : moves) {
            ChessBoard clonedBoard = board.clone();
            ChessPiece atEnd = clonedBoard.getPiece(move.getEndPosition());
            makeMoveHelper(move, clonedBoard, chessPiece);
            if(!checkCalculator(chessPiece.getTeamColor(), clonedBoard)){
                movesValid.add(move);
            }
            undoMoveHelper(move, clonedBoard, chessPiece, atEnd);
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

        //      Likely will use this method to help implement the isInCheck, isInCheckmate, and isInStalemate
        //      methods. Iterate over the valid moves and create a temporary copy of the board using the .clone()
        //      method (scrapping it after each iteration), and make the move on that board, checking to see if
        //      that move would put your king in check, if your king is in check and if your move would get your
        //      king out of check or not.

            if(isInCheck()) {
                add moves that get me out of check
            } else {
                add moves that keep me out of check
            }
```````````*/

        // check to see if a move gets you in check
        // may consider getBoard.clone()
        // clone board for each iteration, calling isInCheck() on each of them, and if none get me out of check,
        // it is checkmate

        // may at some point alter the clone() method here, as it isn't giving me the deep copies I want
        //      (which is why I created the undoMoveHelper() method). Code runs fine, but may no longer need
        //      the clone() method here, as currently it is redundant code. May fix one or both of the methods
        //      at some point, but for now, the code works just fine, and I'm not gonna touch it for right now.
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



    public void undoMoveHelper(ChessMove move, ChessBoard board1, ChessPiece myPiece, ChessPiece theirPiece) {
        board1.addPiece(move.getStartPosition(), myPiece);
        board1.addPiece(move.getEndPosition(), theirPiece);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return isWhiteInCheck == chessGame.isWhiteInCheck
                && isBlackInCheck == chessGame.isBlackInCheck
                && Objects.equals(board, chessGame.board)
                && teamTurn == chessGame.teamTurn
                && isGameOver == chessGame.isGameOver;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, isWhiteInCheck, isBlackInCheck, isGameOver);
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

        //     Check if move is valid or not, if a piece is there or not, and if it is your turn or not.
        //     If it is an illegal move, throw the exception.
        //     Otherwise, execute the move on the original board. Set flags for if teams are in check or not.
        //     At the end, set the next player's turn.
        if(board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("Nothing here, Dummy!");
        }
        if(board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Not your turn, Moron!");
        }
        this.isGameOver = isGameOver();
        if(isGameOver){
            throw new InvalidMoveException("The game is now over. No further moves can be made.");
        }
        if(!validMoves(move.getStartPosition()).contains(move)){
            throw new InvalidMoveException("You can't make that move, Stupid!");
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
        /*  use the board to check if the passed in team is in check and set it in the class.

        v.	Loop over opponent pieces
        1.	For each opponent piece, test if piece can move to the spot where your king is
        2.	If No, continue to the next opponent piece
        3.	If Yes, exit loop (possible move is not valid); destroy cloned grid. Rether to Step II.b
        */
        Collection<ChessPosition> positions = board1.getChessPositions();
        for(ChessPosition position : positions){
            if(position.getRow() -1 < board1.getBoardSize()
                    || position.getColumn() -1 < board1.getBoardSize()
                    || position.getRow() > 0
                    || position.getColumn() > 0){
                if(board1.getPiece(position) != null){
                    if(isEnemyPiece(teamColor, board1, position)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isEnemyPiece(TeamColor teamColor, ChessBoard board1, ChessPosition position) {
        if(board1.getPiece(position).getTeamColor() == opponentTeam(teamColor)){
            ChessPiece enemyPiece = board1.getPiece(position);
            Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(board1, position);
            if(enemyMoves(board1, enemyMoves, enemyPiece)){
                return true;
            }
        }
        return false;
    }

    private static boolean enemyMoves(ChessBoard board1, Collection<ChessMove> enemyMoves, ChessPiece enemyPiece) {
        for(ChessMove enemyMove : enemyMoves){
            if(board1.getPiece(enemyMove.getEndPosition()) != null){
                if(enemyCanCaptureYourKing(board1, enemyPiece, enemyMove)){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean enemyCanCaptureYourKing(ChessBoard board1, ChessPiece enemyPiece, ChessMove enemyMove) {
        if(board1.getPiece(enemyMove.getEndPosition()).getTeamColor() != enemyPiece.getTeamColor()) {
            if(board1.getPiece(enemyMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
                return true;
            }
        }
        return false;
    }

    // create a new helper method to check to see if all validMoves for a team is empty
    // return false if I find a single valid move

    public boolean noMovesLeft(TeamColor teamColor) {
        Collection<ChessPosition> positions = board.getChessPositions();
        int counter = 0;
        for(ChessPosition position : positions) {
            if(board.getPiece(position) != null) {
                ChessPiece piece = board.getPiece(position);
                Collection<ChessMove> moves = piece.pieceMoves(board,position);
                counter = checkIfMovesAreValid(teamColor, moves, piece, counter);
            }
        }
        if(counter == 0){
            return true;
        }
        return false;
    }

    private int checkIfMovesAreValid(TeamColor teamColor, Collection<ChessMove> moves, ChessPiece piece, int counter) {
        for(ChessMove move : moves){
            if(piece.getTeamColor() == teamColor){
                if(validMoves(move.getStartPosition()).contains(move)){
                    counter = getCounter(moves, counter);
                }
            }
        }
        return counter;
    }

    private static int getCounter(Collection<ChessMove> moves, int counter) {
        for(int i = 0; i <= moves.size(); i++) {
            counter += 1;
        }
        return counter;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    // TODO: possibly set isGameOver to true here.
    public boolean isInCheckmate(TeamColor teamColor) {
        // validMoves() is empty, and isInCheck == true
        return noMovesLeft(teamColor) && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    // TODO: possibly set isGameOver to true here
    public boolean isInStalemate(TeamColor teamColor) {
        // same drill as in isInCheckmate()
        return noMovesLeft(teamColor) && !isInCheck(teamColor);
        // is in stalemate if no possible valid moves (validMoves() for all pieces in a specific team is empty)
        // and !isInCheck()
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */

    public void setBoard(ChessBoard board) {
        this.board = board;
        this.isWhiteInCheck = checkCalculator(TeamColor.WHITE, board);
        this.isBlackInCheck = checkCalculator(TeamColor.BLACK, board);
        setTeamTurn(getTeamTurn());
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */

    public ChessBoard getBoard() {
        this.isWhiteInCheck = checkCalculator(TeamColor.WHITE, board);
        this.isBlackInCheck = checkCalculator(TeamColor.BLACK, board);
        this.teamTurn = getTeamTurn();
        setTeamTurn(getTeamTurn());
        return board;
    }

    // TODO: create a boolean that checks if a player has resigned or not, in which case, end the game, make sure no
    //  more moves can be made.

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
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