package ui;

import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.Game;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ClientWareHouse {
    // Optional: This is just for fun!
    private Boolean isSarcasticText = false;
    private int counter = 0;
    private Boolean bootUser = false;
    private Boolean isLoggedIn = false;
    private ChessClient client;

    public ClientWareHouse(ChessClient client){
        this.client = client;
    }

    protected static void sarcasticLogin(Integer counter) throws ResponseException {
        if(counter == 10){
            System.out.println("You know what? I'm done here!");
            System.out.println("If you're not going to take this seriously, " +
                    "then you don't deserve to play!");
            System.out.println("I am making an executive decision here, and closing this session myself.");
            System.out.println("Come back when you're ready to behave and actually take this seriously!");
        } else {
            System.out.println("Ok, this is getting ridiculous. Just Enter 1 or 2 for crying out loud!");
            System.out.println("It's not rocket science! It's just tapping two keys on a keyboard.");
            System.out.println("It's so simple! Heck, a BABY could do it! Please stop wasting my time!");
        }
    }

    protected static void notLoggedInHelpSarcastic(){
        System.out.println("Oh, so you need help, do you?");
        System.out.println("Well, and here I was assuming that this lovely little menu was straightforward enough");
        System.out.println(" for any normal human being to understand. Thank you for proving me wrong.");
        System.out.println("Anyway, since you somehow can't understand these simple instructions, I guess I'll");
        System.out.println("  explain this again in a way that your simple minds can understand: \n");
        System.out.println("  Enter 1 to login to play our magical chess game.");
        System.out.println("  Enter 2 to register a new player if you're new around here and haven't played yet ");
        System.out.println("    (in which case, why are you even reading this? My sarcasm doesn't unlock unless some" +
                " cheeky monkey decided to screw around with the login prompt. ");
        System.out.println("    If you are that cheeky monkey, why the heck did you try to log in if you don't have" +
                "an existing account yet?!)");
        System.out.println("  Enter 3 to quit out of this program (and to get me to shut up if you've unlocked my " +
                "built-in sarcasm).");
        System.out.println("  Enter 4 if somehow you STILL can't understand what we're asking for you," +
                " in which case, I'll just display this message all over again. I've got all day people.\n ");
        System.out.println(" Anyway, here's your stupid menu again. At least TRY to understand it this time...\n");
    }

    protected static void sarcasticRegister() throws ResponseException {
        System.out.println("Oh thank goodness! Someone new.");
        System.out.println("Let's hope you're not as cheeky as the last person who tried to log in. Have fun!");
    }

    protected static void okWiseGuy() {
        System.out.println("Ok, who's the wise guy who tried to promote his pawn to a king, eh?");
        System.out.println("While I appreciate the attempt to sow a little bit of anarchy into the mix here, " +
                "unfortunately, promoting a pawn to a king is against the rules.");
        System.out.println("In other words... \n");
        System.out.println("Error: not a valid promotion.");
    }

    protected static ChessPiece getPromotionPieceClient(Boolean canPromote, ChessPiece chessPiece, Scanner scanner) {
        ChessPiece promotionPiece = null;
        if(canPromote) {
            System.out.println("Enter the piece type you want to promote your pawn to (can be any piece except for " +
                    "KING and PAWN): ");
            String inputPromotionPiece = scanner.nextLine().toUpperCase();
            while(inputPromotionPiece.isBlank()) {
                System.out.println("Error: if you are moving a pawn to the opposite side of the board, it has to be " +
                        "promoted to something.");
                inputPromotionPiece = getInputPromotionPiece(scanner);
            }
            while (inputPromotionPiece.equals("KING")){
                ClientWareHouse.okWiseGuy();
                inputPromotionPiece = getInputPromotionPiece(scanner);
            }
            while(!inputPromotionPiece.equals("QUEEN") &&
                    !inputPromotionPiece.equals("ROOK") &&
                    !inputPromotionPiece.equals("KNIGHT") &&
                    !inputPromotionPiece.equals("BISHOP") &&
                    !inputPromotionPiece.equals("KING")){
                System.out.println("Error: not a valid promotion.");
                inputPromotionPiece = getInputPromotionPiece(scanner);
            }
            if(inputPromotionPiece.equals("QUEEN")){
                promotionPiece = new ChessPiece(chessPiece.getTeamColor(), ChessPiece.PieceType.QUEEN);
            } else if(inputPromotionPiece.equals("ROOK")){
                promotionPiece = new ChessPiece(chessPiece.getTeamColor(), ChessPiece.PieceType.ROOK);
            } else if(inputPromotionPiece.equals("KNIGHT")){
                promotionPiece = new ChessPiece(chessPiece.getTeamColor(), ChessPiece.PieceType.KNIGHT);
            } else if(inputPromotionPiece.equals("BISHOP")){
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

    protected static void displayGamePlayMenu(){
        System.out.println("Choose an option: ");
        System.out.println("  1. Redraw Chess Board");
        System.out.println("  2. Make Move");
        System.out.println("  3. Highlight Legal Moves");
        System.out.println("  4. Resign");
        System.out.println("  5. Leave");
        System.out.println("  6. Help");
    }

    protected static void gamePlayHelp() throws ResponseException{
        System.out.println("Enter 1 to redraw the game board.");
        System.out.println("Enter 2 to make a move.");
        System.out.println("Enter 3 to highlight all legal moves for a specific chess piece.");
        System.out.println("Enter 4 to forfeit, ending the game.");
        System.out.println("Enter 5 to leave the game.");
        System.out.println("Enter 6 to see this message again.\n");
        displayGamePlayMenu();
    }

    protected static void notLoggedInHelp(){
        System.out.println("Choose an option: ");
        System.out.println("  1. Login");
        System.out.println("  2. Register");
        System.out.println("  3. Quit");
        System.out.println("  4. Help");
    }

    protected static void loggedInHelp(){
        System.out.println("Welcome to Chess!");
        System.out.println("Choose an option: ");
        System.out.println("  1. Create Game");
        System.out.println("  2. List Games");
        System.out.println("  3. Play Game");
        System.out.println("  4. Observe Game");
        System.out.println("  5. Logout");
        System.out.println("  6. Help");
    }

    // WEBSOCKET STUFF


    protected static void displayNotification(String message) {
        NotificationMessage serverMessage = new Gson().fromJson(message, NotificationMessage.class);
        System.out.print(SET_TEXT_COLOR_GREEN);
        System.out.println(serverMessage.getMessage());
        System.out.print(RESET_TEXT_COLOR);
    }

    public static void displayError(String message){
        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
        System.out.print(SET_TEXT_COLOR_RED);
        System.out.println(errorMessage.getErrorMessage());
        System.out.print(RESET_TEXT_COLOR);
    }

}
