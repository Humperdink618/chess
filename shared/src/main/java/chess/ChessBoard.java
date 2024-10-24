package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return boardSize == that.boardSize && Objects.deepEquals(chessSquares, that.chessSquares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardSize, Arrays.deepHashCode(chessSquares));
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "boardSize=" + boardSize +
                ", chessSquares=" + Arrays.toString(chessSquares) +
                '}';
    }

    private int boardSize = 8;

    private ChessPiece[][] chessSquares = new ChessPiece[boardSize][boardSize];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessSquares[position.getRow() -1][position.getColumn() - 1] = piece;
    }

    public void removePiece(ChessPosition position){
        chessSquares[position.getRow() -1][position.getColumn() - 1] = null;
    }

    public Collection<ChessPosition> getChessPositions() {
        Collection<ChessPosition> chessPositions = new HashSet<>();
        for(int x = 1; x <= 8; x++){
            for(int y = 1; y <= 8; y++){
                // get row and column
                ChessPosition currPos = new ChessPosition(x,y);
                chessPositions.add(currPos);
            }
        }
        return chessPositions;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */

    public ChessPiece getPiece(ChessPosition position) {
        return chessSquares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Team White
        ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        // Team Black
        ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

        for(int y = 1; y <= 8; y++){
            if(y == 1 || y == 8){
                addPiece(new ChessPosition(1, y), whiteRook);
                addPiece(new ChessPosition(8,y), blackRook);
            } else if(y == 2 || y == 7){
                addPiece(new ChessPosition(1,y), whiteKnight);
                addPiece(new ChessPosition(8,y), blackKnight);
            } else if(y == 3 || y == 6){
                addPiece(new ChessPosition(1,y), whiteBishop);
                addPiece(new ChessPosition(8,y), blackBishop);
            } else if(y == 4){
                addPiece(new ChessPosition(1,y), whiteQueen);
                addPiece(new ChessPosition(8,y), blackQueen);
            } else {
                addPiece(new ChessPosition(1,y), whiteKing);
                addPiece(new ChessPosition(8,y), blackKing);
            }
            addPiece(new ChessPosition(2, y), whitePawn);
            addPiece(new ChessPosition(7, y), blackPawn);
        }
    }

    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public ChessBoard clone() {
        try {
            return (ChessBoard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}