package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessboard {

    public enum boardColor {
        WHITE,
        BLACK,
    }

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        // draw white board
        boardColor white = boardColor.WHITE;
        drawHeaders(out, white);
        drawChessBoard(out, white);
        drawHeaders(out, white);

        // draw division
        drawChessBoardDivision(out);

        // draw black board
        boardColor black = boardColor.BLACK;
        drawHeaders(out, black);
        drawChessBoard(out, black);
        drawHeaders(out, black);
    }

    private static boolean isWhite(boardColor color) {
        return color == boardColor.WHITE;
    }

    private static void drawHeaders(PrintStream out, boardColor color) {

        setLightGrey(out);

        String[] colHeaders = { "a", "b", "c", "d", "e", "f", "g", "h" };
        out.print(EMPTY.repeat(3));
        if(isWhite(color)) {
            //out.println(EMPTY + " a  b  c  d  e  f  g  h " + EMPTY); // column headers (col name)
            for(int boardCol = 0; boardCol < 8; boardCol++){
                drawHeader(out, colHeaders[boardCol]);
            }
        } else {
            for(int boardCol = 7; boardCol > -1; boardCol--){
                drawHeader(out, colHeaders[boardCol]);
            }
        }
        out.print(EMPTY.repeat(3));

        resetColor(out);
    }

    private static void drawHeader(PrintStream out, String headerText){
        printPadding(out);
        printHeaderText(out, headerText);
        printPadding(out);
    }

    private static void printHeaderText(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_TEXT_BOLD);

        out.print(headerText);

        setLightGrey(out);
    }

    private static void drawChessBoard(PrintStream out, boardColor color) {

        // note: first draw the board w/o the chesspieces, then pass in a matrix from ChessClient to populate
        // this chessboard with the right chesspieces in the right positions (use default board config for Phase 5)
        setLightGrey(out);

        if(isWhite(color)) {
            for(int j = 7; j > -1; j--){
                createRowWithHeaders(out, color, j);
                setLightGrey(out);
            }
        } else {
            for(int j = 0; j < 8; j++){
                createRowWithHeaders(out, color, j);
                setLightGrey(out);
            }
        }
    }

    private static void createRowWithHeaders(PrintStream out, boardColor color, int j) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(j + 1);
        String rowHeader = stringBuilder.toString();
        drawHeader(out, rowHeader);
        drawRowOfSquares(out, color, j);
        drawHeader(out, rowHeader);
        resetColor(out);
    }

    private static void drawRowOfSquaresEvenIndex(PrintStream out, boardColor color, int j){
        if(isWhite(color)){
            for(int i = 0; i < 8; i++){
                drawBlackSquareFirst(out, j, i);
            }
        } else {
            for(int i = 0; i < 8; i++){
                drawWhiteSquareFirst(out, j, i);
            }
        }
        setLightGrey(out);
    }

    private static void drawWhiteSquareFirst(PrintStream out, int j, int i) {
        if(i % 2 == 0){
            drawWhiteSquare(out, j, i);
        } else {
            drawBlackSquare(out, j, i);
        }
    }

    private static void drawRowOfSquaresOddIndex(PrintStream out, boardColor color, int j){
        if(isWhite(color)){
            for(int i = 0; i < 8; i++){
                drawWhiteSquareFirst(out, j, i);
            }
        } else {
            for(int i = 0; i < 8; i++){
                drawBlackSquareFirst(out, j, i);
            }
        }
        setLightGrey(out);
    }

    private static void drawBlackSquareFirst(PrintStream out, int j, int i) {
        if(i % 2 == 0){
            drawBlackSquare(out, j, i);
        } else {
            drawWhiteSquare(out, j, i);
        }
    }

    private static void drawRowOfSquares(PrintStream out, boardColor color, int j){
        if(j % 2 == 0){
            drawRowOfSquaresEvenIndex(out, color, j);
        } else {
            drawRowOfSquaresOddIndex(out, color, j);
        }
    }

    private static void drawWhiteSquare(PrintStream out, int j, int i){
        // note: will need to eventually pass in a chesspiece here, but for now, just leave it blank
        setVeryLightGrey(out);
        String chessPiece = parseChessPiece(j, i);
        printPadding(out);
        // note: this is just a placeholder for now. When actually putting in pieces, change this value accordingly
        printChessPiece(out, chessPiece, SET_BG_COLOR_VERY_LIGHT_GREY, SET_TEXT_COLOR_VERY_LIGHT_GREY, j, i);
        printPadding(out);
    }

    private static void drawBlackSquare(PrintStream out, int j, int i){
        // note: will need to eventually pass in a chesspiece here, but for now, just leave it blank
        String chessPiece = parseChessPiece(j, i);
        setBlack(out);
        printPadding(out);
        printChessPiece(out, chessPiece, SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLACK, j, i);
        printPadding(out);
    }

    private static void printPadding(PrintStream out) {
        out.print(EMPTY.repeat(1));
    }

    private static ChessPiece getChessPiece(int j, int i) {
        ChessBoard board = ChessClient.chessPiecePositions();
        ChessPosition myPos = new ChessPosition(j + 1, i + 1);
        ChessPiece myPiece = board.getPiece(myPos);
        return myPiece;
    }

    private static boolean isTeamColorWhite(ChessPiece piece){
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    private static String parseChessPiece(int j, int i){
        ChessPiece piece = getChessPiece(j, i);
        if(piece != null){
            ChessPiece.PieceType type = piece.getPieceType();
            if(isTeamColorWhite(piece)){
                if(type == ChessPiece.PieceType.KING){
                    return WHITE_KING;
                } else if(type == ChessPiece.PieceType.QUEEN){
                    return WHITE_QUEEN;
                } else if(type == ChessPiece.PieceType.BISHOP){
                    return WHITE_BISHOP;
                } else if(type == ChessPiece.PieceType.KNIGHT){
                    return WHITE_KNIGHT;
                } else if(type == ChessPiece.PieceType.ROOK){
                    return WHITE_ROOK;
                } else {
                    return WHITE_PAWN;
                }
            } else {
                if(type == ChessPiece.PieceType.KING){
                    return BLACK_KING;
                } else if(type == ChessPiece.PieceType.QUEEN){
                    return BLACK_QUEEN;
                } else if(type == ChessPiece.PieceType.BISHOP){
                    return BLACK_BISHOP;
                } else if(type == ChessPiece.PieceType.KNIGHT){
                    return BLACK_KNIGHT;
                } else if(type == ChessPiece.PieceType.ROOK){
                    return BLACK_ROOK;
                } else {
                    return BLACK_PAWN;
                }
            }
        } else {
            return EMPTY;
        }
    }

    private static void printChessPiece(PrintStream out,
                                        String chessPiece,
                                        String BGColor,
                                        String textColor,
                                        int j,
                                        int i) {
        ChessPiece piece = getChessPiece(j, i);
        out.print(BGColor);
        if(piece != null){
            if(isTeamColorWhite(piece)){
                out.print(SET_TEXT_COLOR_RED);
            } else {
                out.print(SET_TEXT_COLOR_DARK_BLUE);
            }
        }
        out.print(SET_TEXT_BOLD);

        out.print(chessPiece);

        out.print(BGColor);
        out.print(textColor);
    }

    private static void drawChessBoardDivision(PrintStream out){
        setDarkGrey(out);

        out.print(EMPTY.repeat(30));
        resetColor(out);
    }

    private static void setLightGrey(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setDarkGrey(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setVeryLightGrey(PrintStream out) {
        out.print(SET_BG_COLOR_VERY_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_VERY_LIGHT_GREY);
    }

    private static void resetColor(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print('\n');
    }
}