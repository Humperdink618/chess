package ui;

import chess.*;
import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ClientWareHouse {

    private ChessClient client;

    public ClientWareHouse(ChessClient client) {
        this.client = client;
    }

    // NORMAL STATIC METHODS:
    protected static void printErrorMessageAuth(String authToken) {
        String errorMessage = getErrorMessage(authToken);
        if (errorMessage.equals("Error: bad request")) {
            System.out.println("Error: Failed to register user due to invalid username, password, or email.");
        } else if (errorMessage.equals("Error: already taken")) {
            System.out.println(errorMessage);
        } else {
            System.out.println(errorMessage);
        }
    }

    protected static String getInputRegisterCredentials(String prompt, Scanner scanner) {
        String uNPrompt = prompt;
        String userInput = scanner.nextLine();
        userInput = getInputStringAgainBool(userInput.isBlank(), uNPrompt, userInput, scanner);
        return userInput;
    }

    protected static ChessMove getMove(ChessPiece promotionPiece, ChessPosition startPos, ChessPosition endPos) {
        ChessMove move;
        if (promotionPiece == null) {
            move = new ChessMove(startPos, endPos, null);
        } else {
            move = new ChessMove(startPos, endPos, promotionPiece.getPieceType());
        }
        return move;
    }

    protected static String getTryAgain(Scanner scanner) {
        String promptTryAgain = "If you want to try again, Enter 1. If you want to return to the main menu, Enter 2.";
        String inputAnswer = ClientWareHouse.getInputString(promptTryAgain, scanner);
        return inputAnswer;
    }

    protected static String getInputStringAgainBool(boolean isBlank, String x, String inputString, Scanner scanner) {
        while (isBlank) {
            inputString = ClientWareHouse.getInputAgainBecauseInvalid(scanner, x);
        }
        return inputString;
    }

    protected static String getInvalidPosMessage() {
        String isInvalidPos = "Error: Invalid position.";
        return isInvalidPos;
    }

    protected static String checkIfValidGameID(String gameID, String prompt, Collection<Integer> gameIDs,
                                               Scanner scanner) {
        gameID = ClientWareHouse.getInputStringAgainBool(!gameIDs.contains(Integer.parseInt(gameID)) ||
                !ClientWareHouse.isNumeric(gameID), prompt, gameID, scanner);
        return gameID;
    }

    protected static String getErrorMessage(String authToken) {
        HashMap errorMessageMap = new Gson().fromJson(authToken, HashMap.class);
        String errorMessage = errorMessageMap.get("message").toString();
        return errorMessage;
    }

    protected static String getInputPlayerColor(Scanner scanner, String prompt) {
        System.out.println(prompt);
        String playerColor = scanner.nextLine().toUpperCase();
        return playerColor;
    }

    protected static String getInputAgainBecauseInvalid(Scanner scanner, String prompt) {
        promptAgainBecauseInvalid("Error: not a valid option.", prompt);
        String inputString;
        inputString = scanner.nextLine();
        return inputString;
    }

    protected static String getEmailPrompt() {
        String email = "Please enter your Email: ";
        System.out.println(email);
        return email;
    }

    protected static String getPWPrompt() {
        String pWPrompt = "Create your Password: ";
        System.out.println(pWPrompt);
        return pWPrompt;
    }

    protected static String getUNPrompt() {
        String uNPrompt = "Please create a valid username: ";
        System.out.println(uNPrompt);
        return uNPrompt;
    }

    protected static String getInputString(String prompt, Scanner scanner) {
        String inputString;
        System.out.println(prompt);
        inputString = scanner.nextLine();
        return inputString;
    }

    protected static String getPlayerColorAgain(Scanner scanner, String prompt) {
        promptAgainBecauseInvalid("Error: not a valid option.", prompt);
        String playerColor;
        playerColor = scanner.nextLine().toUpperCase();
        return playerColor;
    }

    protected static void promptAgainBecauseInvalid(String x, String prompt) {
        System.out.println(x);
        System.out.println(prompt);
    }

    protected static String getInputChessPositionAgain(Scanner scanner, String prompt) {
        promptAgainBecauseInvalid("Error: not a valid option.", prompt);
        String chessPos;
        chessPos = scanner.nextLine().toLowerCase();
        return chessPos;
    }

    protected static String getChessPos(Scanner scanner, String prompt) {
        System.out.println(prompt);
        String chessPos = scanner.nextLine().toLowerCase();
        return chessPos;
    }

    protected static String inputDoYouWantToResign(Scanner scanner) {
        System.out.println("Are you sure you want to resign? (Y/N): ");
        // Does not cause the user to leave the game.
        String input = scanner.nextLine().toUpperCase();
        return input;
    }

    protected static void printUnauthorizedErrorMessage(String errorMessage) {
        if (errorMessage.equals("Error: unauthorized")) {
            System.out.println(errorMessage);
        }
    }

    protected static String getHLPrompt() {
        String hLPrompt = "Enter the piece's position in the form b2 (a letter from 'a' to 'h' " +
                "followed by a number from 1 to 8): ";
        return hLPrompt;
    }

    protected static StringBuilder getIndividualGameData(GameData game) {
        StringBuilder individualGameData = new StringBuilder();
        individualGameData.append(" " + game.whiteUsername() + ", ");
        individualGameData.append(game.blackUsername() + ", " + game.gameName());
        return individualGameData;
    }

    protected static String getObservePrompt() {
        String observePrompt = "Pick a game you want to observe: ";
        System.out.println(observePrompt);
        return observePrompt;
    }

    protected static String getGameNameAgainBecauseInvalid(String gameName, Scanner scanner) {
        gameName = ClientWareHouse.getInputStringAgainBool(gameName.isBlank() ||
                !ClientWareHouse.isNumeric(gameName), "Choose a game to observe: ", gameName, scanner);
        return gameName;
    }

    protected static ChessPiece getPromotionPieceClient(Boolean canPromote, ChessPiece chessPiece, Scanner scanner) {
        ChessPiece promotionPiece = null;
        if (canPromote) {
            System.out.println("Enter the piece type you want to promote your pawn to (can be any piece except for " +
                    "KING and PAWN): ");
            String inputPromotionPiece = scanner.nextLine().toUpperCase();
            while (inputPromotionPiece.isBlank()) {
                System.out.println("Error: if you are moving a pawn to the opposite side of the board, it has to be " +
                        "promoted to something.");
                inputPromotionPiece = getInputPromotionPiece(scanner);
            }
            while (inputPromotionPiece.equals("KING")) {
                SarcasticClient.okWiseGuy();
                inputPromotionPiece = getInputPromotionPiece(scanner);
            }
            while (!inputPromotionPiece.equals("QUEEN") &&
                    !inputPromotionPiece.equals("ROOK") &&
                    !inputPromotionPiece.equals("KNIGHT") &&
                    !inputPromotionPiece.equals("BISHOP") &&
                    !inputPromotionPiece.equals("KING")) {
                System.out.println("Error: not a valid promotion.");
                inputPromotionPiece = getInputPromotionPiece(scanner);
            }
            if (inputPromotionPiece.equals("QUEEN")) {
                promotionPiece = new ChessPiece(chessPiece.getTeamColor(), ChessPiece.PieceType.QUEEN);
            } else if (inputPromotionPiece.equals("ROOK")) {
                promotionPiece = new ChessPiece(chessPiece.getTeamColor(), ChessPiece.PieceType.ROOK);
            } else if (inputPromotionPiece.equals("KNIGHT")) {
                promotionPiece = new ChessPiece(chessPiece.getTeamColor(), ChessPiece.PieceType.KNIGHT);
            } else if (inputPromotionPiece.equals("BISHOP")) {
                promotionPiece = new ChessPiece(chessPiece.getTeamColor(), ChessPiece.PieceType.BISHOP);
            }
        }
        return promotionPiece;
    }

    protected static String getInputPromotionPiece(Scanner scanner) {
        String inputPromotionPiece;
        System.out.println("Enter the piece type you want to promote your pawn to (can be any piece except" +
                " for KING and PAWN): ");
        inputPromotionPiece = scanner.nextLine().toUpperCase();
        return inputPromotionPiece;
    }

    protected static void displayGamePlayMenu() {
        System.out.println("Choose an option: ");
        System.out.println("  1. Redraw Chess Board");
        System.out.println("  2. Make Move");
        System.out.println("  3. Highlight Legal Moves");
        System.out.println("  4. Resign");
        System.out.println("  5. Leave");
        System.out.println("  6. Help");
    }

    protected static void gamePlayHelp() throws ResponseException {
        System.out.println("Enter 1 to redraw the game board.");
        System.out.println("Enter 2 to make a move.");
        System.out.println("Enter 3 to highlight all legal moves for a specific chess piece.");
        System.out.println("Enter 4 to forfeit, ending the game.");
        System.out.println("Enter 5 to leave the game.");
        System.out.println("Enter 6 to see this message again.\n");
        displayGamePlayMenu();
    }

    protected static void notLoggedInHelp() {
        System.out.println("Choose an option: ");
        System.out.println("  1. Login");
        System.out.println("  2. Register");
        System.out.println("  3. Quit");
        System.out.println("  4. Help");
    }

    protected static void loggedInHelp() {
        System.out.println("Welcome to Chess!");
        System.out.println("Choose an option: ");
        System.out.println("  1. Create Game");
        System.out.println("  2. List Games");
        System.out.println("  3. Play Game");
        System.out.println("  4. Observe Game");
        System.out.println("  5. Logout");
        System.out.println("  6. Help");
    }

    protected static void displayInvalidMoveObserverMessage() {
        System.out.println("Error: you can't move a piece if you are just observing and not actually " +
                "playing the game.");
    }

    protected static String promptForStartPos() {
        String prompt1 = "Enter the piece's start position in the form b2" +
                " (a letter from 'a' to 'h' followed by a number from 1 to 8): ";
        return prompt1;
    }

    protected static String getStartPrompt() {
        String startPrompt = "Enter the piece's start position in the form b2 (a letter from 'a' to 'h' " +
                "followed by a number from 1 to 8): ";
        return startPrompt;
    }

    protected static String getChessPosAgain(String chessPos, String hLPrompt, Scanner scanner) {
        while (chessPos.isBlank()) {
            chessPos = getInputChessPositionAgain(scanner, hLPrompt);
        }
        return chessPos;
    }

    protected static Integer getNewGameID(String gameID, Integer newID,
                                          Collection<Integer> gameIDs, Collection<GameData> gameDataList) {
        for (int id : gameIDs) {
            if (Integer.parseInt(gameID) == id) {
                newID = ClientWareHouse.getGameIDFromGameDataList(id, gameID, gameDataList);
                if (newID != 0) {
                    break;
                }
            }
        }
        return newID;
    }

    protected static String promptForEndPos() {
        String prompt2 = "Where would you like to move this piece? (Enter the piece's end position in the form b2 " +
                "(a letter from 'a' to 'h' followed by a number from 1 to 8): ";
        return prompt2;
    }

    protected static Integer getGameIDFromGameDataList(int id, String gameID, Collection<GameData> gameDataList) {
        for (GameData game : gameDataList) {
            if (Integer.parseInt(gameID) == game.gameID()) {
                return id;
            }
        }
        return 0;
    }

    protected static String getGameIDAgainIfInvalid(String gameID, Scanner scanner) {
        while (gameID.isBlank() || !ClientWareHouse.isNumeric(gameID)) {
            gameID = ClientWareHouse.getInputAgainBecauseInvalid(scanner, "Pick a game you want to play: ");
        }
        return gameID;
    }

    protected static Boolean checkIfCanPromote(ChessPiece chessPiece, ChessPosition endPos) {
        Boolean canPromote;
        if(chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE && endPos.getRow() == 8){
            canPromote = true;
        } else {
            canPromote = chessPiece.getTeamColor() == ChessGame.TeamColor.BLACK && endPos.getRow() == 1;
        }
        return canPromote;
    }

    protected static void printErrorMessageJoinGame(String joinMessage) {
        String errorMessage = getErrorMessage(joinMessage);
        if(errorMessage.equals("Error: unauthorized") || errorMessage.equals("Error: already taken")) {
            System.out.println(errorMessage);
        } else if(errorMessage.equals("Error: bad request")){
            System.out.println("Error: Join failed due to poor user input.");
        } else {
            System.out.println(errorMessage);
        }
    }


    protected static void getErrorMessageLogin(String authToken) {
        String errorMessage = getErrorMessage(authToken);
        if(errorMessage.equals("Error: unauthorized")) {
            System.out.println("Error: invalid username or password.");
        } else {
            System.out.println(errorMessage);
        }
    }

    protected static void checkIfGameSuccessfullyCreated(String gameID) {
        if(!isNumeric(gameID)){
            String errorMessage = getErrorMessage(gameID);
            if(errorMessage.equals("Error: unauthorized")) {
                System.out.println(errorMessage);
            } else if(errorMessage.equals("Error: bad request")){
                System.out.println("Error: Failed to create game due to poor user input.");
            } else {
                System.out.println(errorMessage);
            }
        } else {
            System.out.println("Game successfully created!");
        }
    }


    protected static String getInputGameID(Scanner scanner, Collection<Integer> gameIDs) {
        String gameID = ClientWareHouse.getInputString("Pick a game you want to play: ", scanner);
        gameID = ClientWareHouse.getGameIDAgainIfInvalid(gameID, scanner);
        gameID = ClientWareHouse.checkIfValidGameID(gameID, "Pick a game you want to play: ", gameIDs, scanner);
        return gameID;
    }

    protected static String getPlayerColor(Scanner scanner) {
        String playerColorPrompt = "Choose what team you wish to play (White or Black): ";
        String playerColor = ClientWareHouse.getInputPlayerColor(scanner, playerColorPrompt);
        while(playerColor.isBlank()){
            playerColor = ClientWareHouse.getPlayerColorAgain(scanner, playerColorPrompt);
        }
        while(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
            playerColor = ClientWareHouse.getPlayerColorAgain(scanner, playerColorPrompt);
        }
        return playerColor;
    }

    protected static String getPw(Scanner scanner) {
        String inputPW;
        inputPW = getInputString("Enter your Password: ", scanner);
        while (inputPW.isBlank()) {
            inputPW = getInputAgainBecauseInvalid(scanner, "Enter your Password: ");
        }
        return inputPW;
    }

    protected static String getUn(Scanner scanner) {
        String inputUN;
        inputUN = getInputString("Please enter your username: ", scanner);
        while (inputUN.isBlank()) {
            inputUN = getInputAgainBecauseInvalid(scanner, "Please enter your username: ");
        }
        return inputUN;
    }

    // WEBSOCKET STUFF
    protected static void displayNotification(String message) {
        NotificationMessage serverMessage = new Gson().fromJson(message, NotificationMessage.class);
        System.out.print(SET_TEXT_COLOR_GREEN);
        System.out.println(serverMessage.getMessage());
        System.out.print(RESET_TEXT_COLOR);
    }

    public static void displayError(String message) {
        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
        System.out.print(SET_TEXT_COLOR_RED);
        System.out.println(errorMessage.getErrorMessage());
        System.out.print(RESET_TEXT_COLOR);
    }

    // note: I am putting this here so that it can be used by both my ChessClient AND my ServerFacadeTests
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected static void drawHighlightedBoard(String color, ChessBoard board, ChessPosition inputPos, ChessGame game){
        Collection<ChessPosition> chessPositions = board.getChessPositions();
        for (ChessPosition position : chessPositions) {
            if (position.getRow() == inputPos.getRow() && position.getColumn() == inputPos.getColumn()) {
                //  make this code grab the most up-to-date chessboard/chess game
                DrawHighlightedChessBoard drawBoard = new DrawHighlightedChessBoard(game, color);
                drawBoard.runHighlight(inputPos);
            }
        }
    }
}
