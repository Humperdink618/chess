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

    // has to have headers for rows ad columns; has to look like a valid chessboard
}
