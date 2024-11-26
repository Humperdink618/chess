package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class DrawChessboard {

    private static ChessBoard board;
   // private static Integer checkIfHighlight;
    private static ChessGame game;
    private static String playerColor;

    public enum BoardColor {
        WHITE,
        BLACK,
    }

    //public DrawChessboard(ChessGame chessGame, String playerColor, Integer checkIfHighlight){
    public DrawChessboard(ChessGame chessGame, String playerColor){
        this.game = chessGame;
        this.board = game.getBoard();
        //this.checkIfHighlight = checkIfHighlight;
        this.playerColor = playerColor;
    }
    // TODO: (OPTIONAL: not sure if I would need to do this or not, but if I so desired (and if I have time),
    //  maybe add some functionality where if a KING is in Check or Checkmate, it and the piece attacking it's
    //  squares turn red or something (same with runHighLight())).

    public void run() {
        // note: will eventually need to pass in the chosen player's color and only print out one side of the board
        // based on that player's color (observers will always view from white's perspective).

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        if(playerColor.equals("WHITE")) {
            // draw white board
            BoardColor white = BoardColor.WHITE;
            drawHeaders(out, white);
            drawChessBoard(out, white, null);
            drawHeaders(out, white);
        }

        // draw division
  //      drawChessBoardDivision(out);

        else if(playerColor.equals("BLACK")) {
            // draw black board
            BoardColor black = BoardColor.BLACK;
            drawHeaders(out, black);
            drawChessBoard(out, black, null);
            drawHeaders(out, black);
        }
        else {
            // draw division
            drawChessBoardDivision(out);
            System.out.println("Error: Not a valid team color.");
        }
    }

    public static boolean isWhite(BoardColor color) {
        return color == BoardColor.WHITE;
    }

    public static void drawHeaders(PrintStream out, BoardColor color) {

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

    public static void drawHeader(PrintStream out, String headerText){
        printPadding(out);
        printHeaderText(out, headerText);
        printPadding(out);
    }

    public static void printHeaderText(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_TEXT_BOLD);

        out.print(headerText);

        setLightGrey(out);
    }

    private static void drawChessBoard(PrintStream out, BoardColor color, ChessPosition pos) {

        // note: first draw the board w/o the chesspieces, then pass in a matrix from ChessClient to populate
        // this chessboard with the right chesspieces in the right positions (use default board config for Phase 5)
        setLightGrey(out);

        if(isWhite(color)) {
            for(int j = 7; j > -1; j--){
                createRowWithHeaders(out, color, j, pos);
                setLightGrey(out);
            }
        } else {
            for(int j = 0; j < 8; j++){
                createRowWithHeaders(out, color, j, pos);
                setLightGrey(out);
            }
        }
    }

    private static void createRowWithHeaders(PrintStream out, BoardColor color, int j, ChessPosition pos) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(j + 1);
        String rowHeader = stringBuilder.toString();
        drawHeader(out, rowHeader);
        drawRowOfSquares(out, color, j, pos);
        drawHeader(out, rowHeader);
        resetColor(out);
    }

    private static void drawRowOfSquaresEvenIndex(PrintStream out, BoardColor color, int j, ChessPosition pos){

        if(isWhite(color)){
            for(int i = 0; i < 8; i++){
                drawBlackSquareFirst(out, j, i);
            }
        } else {
            for(int i = 7; i > -1; i--){
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

    private static void drawRowOfSquaresOddIndex(PrintStream out, BoardColor color, int j, ChessPosition pos) {

        if (isWhite(color)) {
            for (int i = 0; i < 8; i++) {
                drawWhiteSquareFirst(out, j, i);
            }
        } else {
            for (int i = 7; i > -1; i--) {
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

    private static void drawRowOfSquares(PrintStream out, BoardColor color, int j, ChessPosition pos){
        if(isWhite(color)) {
            if (j % 2 == 0) {
                drawRowOfSquaresEvenIndex(out, color, j, pos);
            } else {
                drawRowOfSquaresOddIndex(out, color, j, pos);
            }
        } else {
            if (j % 2 == 0) {
                drawRowOfSquaresOddIndex(out, color, j, pos);
            } else {
                drawRowOfSquaresEvenIndex(out, color, j, pos);
            }
        }
    }

    public static void drawWhiteSquare(PrintStream out, int j, int i){
        setVeryLightGrey(out);
        String chessPiece = parseChessPiece(j, i);
        printPadding(out);
        printChessPiece(out, chessPiece, SET_BG_COLOR_VERY_LIGHT_GREY, SET_TEXT_COLOR_VERY_LIGHT_GREY, j, i);
        printPadding(out);
    }

    public static void drawBlackSquare(PrintStream out, int j, int i){
        String chessPiece = parseChessPiece(j, i);
        setBlack(out);
        printPadding(out);
        printChessPiece(out, chessPiece, SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLACK, j, i);
        printPadding(out);
    }

    public static void printPadding(PrintStream out) {
        out.print(EMPTY.repeat(1));
    }

    public static ChessPiece getChessPiece(int j, int i) {
        //ChessBoard board = ChessClient.chessPiecePositions();
        //ChessBoard chessBoard = DrawChessboard.getBoard();
        ChessPosition myPos = new ChessPosition(j + 1, i + 1);
        ChessPiece myPiece = board.getPiece(myPos);
        return myPiece;
    }

    public static boolean isTeamColorWhite(ChessPiece piece){
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    public static String parseChessPiece(int j, int i){
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
                                        String backGroundColor,
                                        String textColor,
                                        int j,
                                        int i) {
        ChessPiece piece = getChessPiece(j, i);
        out.print(backGroundColor);
        if(piece != null){
            if(isTeamColorWhite(piece)){
                out.print(SET_TEXT_COLOR_RED);

            } else {
                out.print(SET_TEXT_COLOR_DARK_BLUE);
            }
        }
        out.print(SET_TEXT_BOLD);
        out.print(chessPiece);
        out.print(backGroundColor);
        out.print(textColor);
    }

    private static void drawChessBoardDivision(PrintStream out){
        setDarkGrey(out);

        out.print(EMPTY.repeat(30));
        resetColor(out);
    }

    public static void setLightGrey(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    public static void setDarkGrey(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    public static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    public static void setVeryLightGrey(PrintStream out) {
        out.print(SET_BG_COLOR_VERY_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_VERY_LIGHT_GREY);
    }

    public static void resetColor(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print('\n');
    }
}