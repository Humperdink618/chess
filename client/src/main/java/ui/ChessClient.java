package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.serverfacade.ServerFacade;

import java.util.*;

public class ChessClient {
    // TODO: implement the Chess client.
    // These are variables which you will need for multiple functions
    private static Scanner scanner = new Scanner(System.in);
    private static String auth = "";
    // TODO: edit this code when I've actually implemented the ServerFacade
    private final ServerFacade serverFacade = new ServerFacade();
    private static Boolean isLoggedIn = false;

    // write code for menu items here
    public static int main(String[] args) {
        System.out.println("Welcome to 240 chess!");
        notLoggedInHelp();
        while (true) {
            if (isLoggedIn) {
                // some thing
            } else {
                boolean quit = notLoggedIn();
                if(quit){
                    return 0;
                }
            }
        }
    }

    private static Boolean notLoggedIn(){
        String input = scanner.nextLine();
        if(input.equals("1")){
            loginUser();
        } else if(input.equals("2")) {
            registerUser();
        } else if(input.equals("3")) {
            return true;
        } else if(input.equals("4")) {
            notLoggedInHelp();
        } else {
            System.out.println("Not a valid option.");
            notLoggedInHelp();
        }
        return false;
    }

    private static void notLoggedInHelp(){
        System.out.println("Choose an option: ");
        System.out.println("  1. Login");
        System.out.println("  2. Register");
        System.out.println("  3. Quit");
        System.out.println("  4. Help");
    }

    private static void loginUser(){
        System.out.println("Please enter your username: ");
        String inputUserName = scanner.nextLine();
        while(inputUserName.isBlank()) {
            // isBlank() returns true if input is empty string or only composed of whitespace characters.
            System.out.println("Please enter your username: ");
            inputUserName = scanner.nextLine();
        }
        System.out.println("Enter your Password: ");
        String inputPassword = scanner.nextLine();
        while(inputPassword.isBlank()) {
            System.out.println("Enter your Password: ");
            inputPassword = scanner.nextLine();
        }
        ServerFacade.login(inputUserName,inputPassword);
        // TODO call server facade login
        //  next get response back and store in a variable
        //  check the variable to see if the login was successful
        //  if it was, change logged in to true
        //  set auth = authtoken returned from result
        //  print out result ("Yay you are in!" or "Boo you are dumb")

    }

    private static void registerUser(){
        System.out.println("Please create a valid username: ");
        String inputUserName = scanner.nextLine();
        while(inputUserName.isBlank()) {
            System.out.println("Please create a valid username: ");
            inputUserName = scanner.nextLine();
        }
        System.out.println("Create your Password: ");
        String inputPassword = scanner.nextLine();
        while(inputPassword.isBlank()) {
            System.out.println("Create your Password: ");
            inputPassword = scanner.nextLine();
        }
        System.out.println("Please enter your Email: ");
        String inputEmail = scanner.nextLine();
        while(inputEmail.isBlank()){
            System.out.println("Please enter your Email: ");
            inputEmail = scanner.nextLine();
        }
        ServerFacade.register(inputUserName,inputPassword, inputEmail);
        // TODO call server facade register
        //  next get response back and store in a variable
        //  check the variable to see if the register was successful
        //  if it was, change logged in to true
        //  set auth = authtoken returned from result
        //  print out result ("Yay you are in!" or "Boo you are dumb")

    }

    private static Boolean LoggedIn(){
        String input = scanner.nextLine();
        if(input.equals("1")){
            createGame();
        } else if(input.equals("2")) {
            //listGames();
        } else if(input.equals("3")) {
            //playGames();
        } else if(input.equals("4")) {
            //observeGame();
        } else if(input.equals("5")) {
            logoutUser();
            return false;
        } else if(input.equals("6")) {
            notLoggedInHelp();
        } else {
            System.out.println("Not a valid option.");
            notLoggedInHelp();
        }
        return true;
    }

    private static void createGame(){
        System.out.println("Create a name for your Chessgame: ");
        String gameName = scanner.nextLine();
        while(gameName.isBlank()){
            System.out.println("Create a name for your Chessgame: ");
            gameName = scanner.nextLine();
        }
        // plug in the authToken given from the register/login
        ServerFacade.create(gameName, auth);
        // TODO call server facade create game
        //  next get response back and store in a variable
        //  check the variable to see if the create game was successful
        //  store gameID but don't print it out
        //  print out result ("Game successfully created" or "Game not created")

    }

    private static void observeGame(){
        // print out the list of games with associated numbers starting at 1 (independent of gameID)
        System.out.println("Choose a game to play: ");
        String gameName = scanner.nextLine();
        while(gameName.isBlank()){
            System.out.println("Choose a game to play: ");
            // print out the list of games with associated numbers starting at 1 (independent of gameID)
            gameName = scanner.nextLine();
        }
        // note: no calling the ServerFacade here. The Client keeps track of which number is associated with which game
        // may want to create a hashset that keeps track of server gameIDs and the ui gameIDs

        // note: gameplay will not be implemented until Phase 6. For now, just display the ChessBoard

        // TODO: figure out gameIDs with associated games to figure out which game to display.
        //   for now, until the above is completed, just print out the board for an unspecified game. Fix this later.
        ChessBoard board = chessPiecePositions();
        //DrawChessboard.main();
    }

    private static void logoutUser(){

        ServerFacade.logout(auth);
        // TODO call server facade logout
        //  next get response back and store in a variable
        //  check the variable to see if the logout was successful
        //  reset auth = ""
        //  print out result ("logout successful!" or "logout failed")
        isLoggedIn = false;
        auth = "";
    }

    private static void LoggedInHelp(){
        System.out.println("Welcome to Chess!");
        System.out.println("Choose an option: ");
        System.out.println("  1. Create Game");
        System.out.println("  2. List Games");
        System.out.println("  3. Play Game");
        System.out.println("  4. Observe Game");
        System.out.println("  5. Logout");
        System.out.println("  6. Help");
    }




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
        // note: this may be a temporary solution, as it may or may not be compatible with Phase 6
        // for now though, it works fine
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        return board;
    }

}
