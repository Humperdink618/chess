package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class ChessClient {
    // TODO: implement the Chess client.

    // write code for menu items here

//    // create matrix for chesspiece locations
//    public static Collection<ChessPiece> chessPiecePositions() {
//        Collection<ChessPiece> chessPieces = new HashSet<>();
//        ChessBoard board = new ChessBoard();
//        board.resetBoard();
//        Collection<ChessPosition> positions = board.getChessPositions();
//        for (ChessPosition position : positions) {
//            chessPieces.add(board.getPiece(position));
//        }
//        return chessPieces;
//    }

    // create matrix for chesspiece locations
    public static ChessBoard chessPiecePositions() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        return board;
    }

}
