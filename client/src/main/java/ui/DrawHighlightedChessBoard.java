package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawHighlightedChessBoard {
    private static ChessBoard board;
    private static ChessGame game;
    private static String playerColor;

    public DrawChessboard.BoardColor boardColor;

    public enum HiLtBoardColor {
        WHITE,
        BLACK,
    }

    public DrawHighlightedChessBoard(ChessGame chessGame, String playerColor){
        this.game = chessGame;
        this.board = game.getBoard();
        this.playerColor = playerColor;
    }

    public void runHighlight(ChessPosition inputPos) {
        // note: will eventually need to pass in the chosen player's color and only print out one side of the board
        // based on that player's color (observers will always view from white's perspective).
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        if(playerColor.equals("WHITE")) {
            // draw white board
            DrawChessboard.BoardColor white = DrawChessboard.BoardColor.WHITE;
            DrawChessboard.drawHeaders(out, white);
            drawChessBoard1(out, white, inputPos);
            DrawChessboard.drawHeaders(out, white);
        }
        // draw division
        //      drawChessBoardDivision(out);
        else if(playerColor.equals("BLACK")) {
            // draw black board
            DrawChessboard.BoardColor black = DrawChessboard.BoardColor.BLACK;
            DrawChessboard.drawHeaders(out, black);
            drawChessBoard1(out, black, inputPos);
            DrawChessboard.drawHeaders(out, black);
        }
        else {
            System.out.println("Error: Not a valid team color");
        }
    }

    private static void drawChessBoard1(PrintStream out, DrawChessboard.BoardColor color, ChessPosition pos) {

        // note: first draw the board w/o the chesspieces, then pass in a matrix from ChessClient to populate
        // this chessboard with the right chesspieces in the right positions (use default board config for Phase 5)
        DrawChessboard.setLightGrey(out);

        if(DrawChessboard.isWhite(color)) {
            for(int j = 7; j > -1; j--){
                createNewRowWithHeaders(out, color, pos, j);
            }
        } else {
            for(int j = 0; j < 8; j++){
                createNewRowWithHeaders(out, color, pos, j);
            }
        }
    }

    private static void createNewRowWithHeaders(
            PrintStream out,
            DrawChessboard.BoardColor color,
            ChessPosition pos,
            int j) {
        createRowWithHeaders1(out, color, j, pos);
        DrawChessboard.setLightGrey(out);
    }

    private static void createRowWithHeaders1(
            PrintStream out,
            DrawChessboard.BoardColor color,
            int j,
            ChessPosition pos) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(j + 1);
        String rowHeader = stringBuilder.toString();
        DrawChessboard.drawHeader(out, rowHeader);
        drawRowOfSquares1(out, color, j, pos);
        DrawChessboard.drawHeader(out, rowHeader);
        DrawChessboard.resetColor(out);
    }

    private static void drawRowOfSquaresEvenIndex1(
            PrintStream out,
            DrawChessboard.BoardColor color,
            int j,
            ChessPosition pos) {

        Collection<ChessMove> highlightMoves = validMoves(pos);
        int x = j + 1;
        if (DrawChessboard.isWhite(color)) {
            //even index
            for (int i = 0; i < 8; i++) {
                int y = i + 1;
                ChessPosition startPos = getStartPos(x, y, highlightMoves, pos);
                ChessPosition endPos = getEndPos(x, y, highlightMoves);
                drawBlackSquareFirstHilight(out, j, i, startPos, endPos, pos);
            }
        } else {
            for (int i = 7; i > -1; i--) {
                int y = i + 1;
                ChessPosition startPos1 = getStartPos(x, y, highlightMoves, pos);
                ChessPosition endPos1 = getEndPos(x, y, highlightMoves);
                drawWhiteSquareFirstHilight(out, j, i, startPos1, endPos1, pos);
            }
        }
        DrawChessboard.setLightGrey(out);
    }

    private static void drawWhiteSquareFirstHilight(
            PrintStream out,
            int j,
            int i,
            ChessPosition startPos,
            ChessPosition endPos,
            ChessPosition pos) {
        int k = pos.getColumn() - 1;
        int z = pos.getRow() - 1;
        if(i % 2 == 0){
            drawWhiteSquareMethod(out, j, i, startPos, endPos, pos, z, k);
        } else {
            drawBlackSquareMethod(out, j, i, startPos, endPos, pos, z, k);
        }
    }

    private static void drawWhiteSquareMethod(PrintStream out,
                                              int j,
                                              int i,
                                              ChessPosition startPos,
                                              ChessPosition endPos,
                                              ChessPosition pos,
                                              int z,
                                              int k) {
        if(startPos == null){
            DrawChessboard.drawWhiteSquare(out, j, i);
        } else if(endPos == null){
            if(j == z && i == k){
                drawYellowSquare(out, j, i);
            } else {
                DrawChessboard.drawWhiteSquare(out, j, i);
            }
        } else if(startPos == pos){
            if(j == z && i == k){
                drawYellowSquare(out, j, i);
            } else {
                drawLightGreenSquare(out, j, i);
            }
        } else {
            DrawChessboard.drawWhiteSquare(out, j, i);
        }
    }

    private static void drawBlackSquareMethod(PrintStream out,
                                              int j,
                                              int i,
                                              ChessPosition startPos,
                                              ChessPosition endPos,
                                              ChessPosition pos,
                                              int z,
                                              int k) {
        if(startPos == null) {
            DrawChessboard.drawBlackSquare(out, j, i);
        } else if(endPos == null){
            if(j == z && i == k){
                drawOrangeSquare(out, j, i);
            } else {
                DrawChessboard.drawBlackSquare(out, j, i);
            }
        } else if (startPos == pos) {
            if(j == z && i == k){
                drawOrangeSquare(out, j, i);
            } else {
                drawDarkGreenSquare(out, j, i);
            }
        } else {
            DrawChessboard.drawBlackSquare(out, j, i);
        }
    }

    private static void drawBlackSquareFirstHilight(
            PrintStream out,
            int j,
            int i,
            ChessPosition startPos,
            ChessPosition endPos,
            ChessPosition pos){
        int k = pos.getColumn() - 1;
        int z = pos.getRow() - 1;
        if(i % 2 == 0){
            drawBlackSquareMethod(out, j, i, startPos, endPos, pos, z, k);
        } else {
            drawWhiteSquareMethod(out, j, i, startPos, endPos, pos, z, k);
        }
    }

    private static void drawRowOfSquaresOddIndex1(
            PrintStream out,
            DrawChessboard.BoardColor color,
            int j,
            ChessPosition pos) {

        Collection<ChessMove> highlightMoves = validMoves(pos);
        int x = j + 1;
        if (DrawChessboard.isWhite(color)) {
            // odd index
            for (int i = 0; i < 8; i++) {
                int y = i + 1;
                ChessPosition startPos = getStartPos(x, y, highlightMoves, pos);
                ChessPosition endPos = getEndPos(x, y, highlightMoves);
                drawWhiteSquareFirstHilight(out, j, i, startPos, endPos, pos);
            }
        } else {
            for (int i = 7; i > -1; i--) {
                int y = i + 1;
                ChessPosition startPos = getStartPos(x, y, highlightMoves, pos);
                ChessPosition endPos = getEndPos(x, y, highlightMoves);
                drawBlackSquareFirstHilight(out, j, i, startPos, endPos, pos);
            }
        }
        DrawChessboard.setLightGrey(out);

    }

    private static ChessPosition getStartPos(int j, int i, Collection<ChessMove> moves, ChessPosition pos){
        for(ChessMove move : moves){
            ChessPosition startPos = move.getStartPosition();
            if(startPos.getRow() == pos.getRow() && startPos.getColumn() == pos.getColumn()){
                return pos;
            } else if(startPos.getRow() == j && startPos.getColumn() == i && startPos != pos){
                return startPos;
            }
        }
        return null;
    }

    private static ChessPosition getEndPos(int j, int i, Collection<ChessMove> moves){
        for(ChessMove move : moves){
            ChessPosition endPos = move.getEndPosition();
            if(endPos.getRow() == j && endPos.getColumn() == i){
                return endPos;
            }
        }
        return null;
    }

    private static void drawRowOfSquares1(
            PrintStream out,
            DrawChessboard.BoardColor color,
            int j,
            ChessPosition pos){
        if(DrawChessboard.isWhite(color)) {
            if (j % 2 == 0) {
                drawRowOfSquaresEvenIndex1(out, color, j, pos);
            } else {
                drawRowOfSquaresOddIndex1(out, color, j, pos);
            }
        } else {
            if (j % 2 == 0) {
                drawRowOfSquaresOddIndex1(out, color, j, pos);
            } else {
                drawRowOfSquaresEvenIndex1(out, color, j, pos);
            }
        }
    }

    private static void drawLightGreenSquare(PrintStream out, int j, int i){
        setLightGreen(out);
        String chessPiece = DrawChessboard.parseChessPiece(j, i);
        DrawChessboard.printPadding(out);
        printChessPiece1(out, chessPiece, SET_BG_COLOR_GREEN, SET_TEXT_COLOR_GREEN, j, i);
        DrawChessboard.printPadding(out);
    }

    private static void drawDarkGreenSquare(PrintStream out, int j, int i){
        setDarkGreen(out);
        String chessPiece = DrawChessboard.parseChessPiece(j, i);
        DrawChessboard.printPadding(out);
        printChessPiece1(out, chessPiece, SET_BG_COLOR_DARK_GREEN, SET_TEXT_COLOR_DARK_GREEN, j, i);
        DrawChessboard.printPadding(out);
    }

    private static void drawYellowSquare(PrintStream out, int j, int i){
        setYellow(out);
        String chessPiece = DrawChessboard.parseChessPiece(j, i);
        DrawChessboard.printPadding(out);
        printChessPiece1(out, chessPiece, SET_BG_COLOR_YELLOW, SET_TEXT_COLOR_YELLOW, j, i);
        DrawChessboard.printPadding(out);
    }

    private static void drawOrangeSquare(PrintStream out, int j, int i){
        setOrange(out);
        String chessPiece = DrawChessboard.parseChessPiece(j, i);
        DrawChessboard.printPadding(out);
        printChessPiece1(out, chessPiece, SET_BG_COLOR_ORANGE, SET_TEXT_COLOR_ORANGE, j, i);
        DrawChessboard.printPadding(out);
    }

    private static Collection<ChessMove> validMoves(ChessPosition pos){
        return game.validMoves(pos);
    }

    private static boolean isTeamColorWhite1(ChessPiece piece){
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    private static void printChessPiece1(PrintStream out,
                                         String chessPiece,
                                         String backGroundColor,
                                         String textColor,
                                         int j,
                                         int i) {
        ChessPiece piece = DrawChessboard.getChessPiece(j, i);
        out.print(backGroundColor);
        if(piece != null){
            if(backGroundColor.equals(SET_BG_COLOR_GREEN)
                    || backGroundColor.equals(SET_BG_COLOR_YELLOW)
                    || backGroundColor.equals(SET_BG_COLOR_DARK_GREEN)
                    || backGroundColor.equals(SET_BG_COLOR_ORANGE)){
                out.print(SET_TEXT_COLOR_BLACK);
            } else if(isTeamColorWhite1(piece)){
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

    private static void setLightGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREEN);
    }

    private static void setYellow(PrintStream out) {
        out.print(SET_BG_COLOR_YELLOW);
        out.print(SET_TEXT_COLOR_YELLOW);
    }

    private static void setOrange(PrintStream out) {
        out.print(SET_BG_COLOR_ORANGE);
        out.print(SET_TEXT_COLOR_ORANGE);
    }
}
