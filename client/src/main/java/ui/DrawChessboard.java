package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessboard {
    // TODO: draw the chessboard
    // Hint: use EscapeSequences.java and TicTacToe.java (on GitHub) to help you write this.
    // Also, may also want to break it up into separate methods.
    // Also also, though not required, you can also pass in a matrix that tells you where the pieces are on a
    // chessboard, (matrix will be created in ChessClient.java) pass that whole matrix into this class,
    // and as I draw my board, I can check to see which positions those pieces are in and plug them into my
    // drawing in those same positions (not required for Phase 5, but helpful for Phase 6).

    // matrix is going to be 0 based (i.e. 1A will be [0,0])

    // have methods for draw white square and for draw black square

    // iterate over headers, column by column
    // perhaps use 8 methods: one for each row
    // for Black, we can iterate it in reverse

    // can also use an ENUM for Black and White to determine if you should iterate normally or in reverse

    // has to have headers for rows and columns; has to look like a valid chessboard

    // Padded characters.
    private static final String PADDING = " ";

    public enum playerColor {
        WHITE,
        BLACK,
    }

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        // draw white board
        playerColor white = playerColor.WHITE;
        drawHeaders(out, white);
        drawChessBoard(out, white);
        drawHeaders(out, white);

        // draw division
        drawChessBoardDivision(out);

//        // draw black board
        playerColor black = playerColor.BLACK;
        drawHeaders(out, black);
        drawChessBoard(out, black);
        drawHeaders(out, black);

    }

    private static boolean isWhite(playerColor color) {
        if(color == playerColor.WHITE) {
            return true;
        }
        return false;
    }

    private static void drawHeaders(PrintStream out, playerColor color) {

        setLightGrey(out);

        String[] colHeaders = { "a", "b", "c", "d", "e", "f", "g", "h" };
        out.print(PADDING.repeat(3));
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
        out.print(PADDING.repeat(3));

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

    private static void drawChessBoard(PrintStream out, playerColor color) {

        // note: first draw the board w/o the chesspieces, then pass in a matrix from ChessClient to populate
        // this chessboard with the right chesspieces in the right positions (use default board config for Phase 5)
        setLightGrey(out);

        if(isWhite(color)) {

            for(int i = 7; i > -1; i--){
                createRowWithHeaders(out, color, i);
                setLightGrey(out);
            }

        } else {
            for(int j = 0; j < 8; j++){
                createRowWithHeaders(out, color, j);
                setLightGrey(out);
            }
        }
        //resetColor(out);
    }

    private static void createRowWithHeaders(PrintStream out, playerColor color, int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i + 1);
        String rowHeader = stringBuilder.toString();
        drawHeader(out, rowHeader);
        drawRowOfSquares(out, color, i);
        drawHeader(out, rowHeader);
        resetColor(out);
    }

    private static void drawRowOfSquaresEvenIndex(PrintStream out, playerColor color){
        if(isWhite(color)){
            for(int i = 0; i < 8; i++){
                if(i % 2 == 0){
                    drawBlackSquare(out, color);
                } else {
                    drawWhiteSquare(out, color);
                }
            }
        } else {
            for(int i = 0; i < 8; i++){
                if(i % 2 == 0){
                    drawWhiteSquare(out, color);
                } else {
                    drawBlackSquare(out, color);
                }
            }
        }
        setLightGrey(out);
    }

    private static void drawRowOfSquaresOddIndex(PrintStream out, playerColor color){
        if(isWhite(color)){
            for(int i = 0; i < 8; i++){
                if(i % 2 == 0){
                    drawWhiteSquare(out, color);
                } else {
                    drawBlackSquare(out, color);
                }
            }
        } else {
            for(int i = 0; i < 8; i++){
                if(i % 2 == 0){
                    drawBlackSquare(out, color);
                } else {
                    drawWhiteSquare(out, color);
                }
            }
        }
        setLightGrey(out);
    }

    private static void drawRowOfSquares(PrintStream out, playerColor color, int j){
        if(j % 2 == 0){
            drawRowOfSquaresEvenIndex(out, color);
        } else {
            drawRowOfSquaresOddIndex(out, color);
        }
    }

    private static void drawWhiteSquare(PrintStream out, playerColor color){
        // note: will need to eventually pass in a chesspiece here, but for now, just leave it blank
        setVeryLightGrey(out);
        printPadding(out);
        // note: this is just a placeholder for now. When actually putting in pieces, change this value accordingly
        printChessPiece(out, " ", SET_BG_COLOR_VERY_LIGHT_GREY, SET_TEXT_COLOR_VERY_LIGHT_GREY, color);
        printPadding(out);

    }

    private static void drawBlackSquare(PrintStream out, playerColor color){
        // note: will need to eventually pass in a chesspiece here, but for now, just leave it blank
        setBlack(out);
        printPadding(out);
        printChessPiece(out, " ", SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLACK, color);
        printPadding(out);
    }

    private static void printPadding(PrintStream out) {
        out.print(PADDING.repeat(1));
    }

    private static void printChessPiece(PrintStream out,
                                        String chessPiece,
                                        String BGColor,
                                        String textColor,
                                        playerColor color) {
        out.print(BGColor);
        if(isWhite(color)){
            out.print(SET_TEXT_COLOR_RED);
        } else {
            out.print(SET_TEXT_COLOR_BLUE);
        }
        out.print(chessPiece);

        out.print(BGColor);
        out.print(textColor);
    }

    private static void drawChessBoardDivision(PrintStream out){
        setDarkGrey(out);

        out.print(PADDING.repeat(30));
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

