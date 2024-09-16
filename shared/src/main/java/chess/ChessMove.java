package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {

        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {

        return startPosition;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {

        return endPosition;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {

        // TODO: at some point, I will need to check to see if pawn promotion is part of
        // TODO: this chess move, and if so, add a getPieceType() function call to get the
        // TODO: type of piece the pawn is being promoted to, as well as differentiate
        // TODO: my return statements to see if I am returning the type of piece the
        // TODO: pawn is being promoted to, or if I am returning a null if there is no
        // TODO: promotion. Right now, it just returns the promotion piece. Will edit later.

        return null;
        //override this in the Pawn subclass
        //throw new RuntimeException("Not implemented");
    }
}
