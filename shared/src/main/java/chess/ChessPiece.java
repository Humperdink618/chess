package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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
        // call the specific function
        if (getPieceType() == PieceType.BISHOP) {
            PieceMovesCalculator bishop = new BishopMovesCalculator();
            return bishop.pieceMoves(board, myPosition);
        }
        else if(getPieceType() == PieceType.QUEEN) {
            PieceMovesCalculator queen = new QueenMovesCalculator();
            return queen.pieceMoves(board, myPosition);
        }
        else if(getPieceType() == PieceType.ROOK) {
            PieceMovesCalculator rook = new RookMovesCalculator();
            return rook.pieceMoves(board, myPosition);
        }
        else if(getPieceType() == PieceType.KING) {
            PieceMovesCalculator king = new KingMovesCalculator();
            return king.pieceMoves(board, myPosition);
        }
        else if(getPieceType() == PieceType.KNIGHT) {
            PieceMovesCalculator knight = new KnightMovesCalculator();
            return knight.pieceMoves(board, myPosition);
        }
        else if(getPieceType() == PieceType.PAWN) {
            PieceMovesCalculator pawn = new PawnMovesCalculator();
            return pawn.pieceMoves(board, myPosition);
        }
        return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
}
