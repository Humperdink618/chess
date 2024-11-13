package ui;

import chess.ChessBoard;
import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import result.ListResult;
import ui.serverfacade.ServerFacade;

import java.util.*;

public class ChessClient {
    // TODO: implement the Chess client.
    // These are variables which you will need for multiple functions
    private Scanner scanner = new Scanner(System.in);
    private String auth = null;
    // TODO: edit this code when I've actually implemented the ServerFacade
    private final String serverURL;
    private final ServerFacade serverFacade;
    private Boolean isLoggedIn = false;
    private Collection<Integer> gameIDs = new HashSet<>();

    public ChessClient(String serverURL){
        serverFacade = new ServerFacade(serverURL);
        this.serverURL = serverURL;
    }

    // write code for menu items here
    public int run() throws ResponseException{
        System.out.println("Welcome to 240 chess!");
        notLoggedInHelp();
        while (true) {
            if (isLoggedIn) {
                loggedInHelp();
                loggedIn();

            } else {
                boolean quit = notLoggedIn();
                if(quit){
                    return 0;
                }
            }
        }
    }

    private Boolean notLoggedIn() throws ResponseException{
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
            System.out.println("Not a valid option.\n");
            notLoggedInHelp();
        }
        return false;
    }

    private void notLoggedInHelp(){
        System.out.println("Choose an option: ");
        System.out.println("  1. Login");
        System.out.println("  2. Register");
        System.out.println("  3. Quit");
        System.out.println("  4. Help");
    }

    private void loginUser() throws ResponseException{
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
        boolean isError = false;
        String authToken = serverFacade.login(inputUserName,inputPassword, isError);
        // TODO call server facade login
        //  next get response back and store in a variable
        //  check the variable to see if the login was successful
        //  if it was, change logged in to true
        //  set auth = authtoken returned from result
        //  print out result ("Yay you are in!" or "Boo you are dumb")
        if(isError){
            System.out.println(authToken);
            System.out.println("If you want to try again, Enter 1. If you want to return to the main menu, Enter 2.");
            String inputAnswer = scanner.nextLine();
            if(inputAnswer.equals("1")){
                loginUser();
            } else if(inputAnswer.equals("2")){
                notLoggedInHelp();
                notLoggedIn();
            } else {
                System.out.println("Oh, so you think you're funny, eh? " +
                        "Well, I guess I'll make the decision FOR you...");
                notLoggedInHelp();
                notLoggedIn();
            }

        } else {
            auth = authToken;
            System.out.println("Login successful!");
            isLoggedIn = true;
        }
    }

    private void registerUser() throws ResponseException{
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
        boolean isError = false;
        String authToken = serverFacade.register(inputUserName,inputPassword, inputEmail, isError);
        // TODO call server facade register
        //  next get response back and store in a variable
        //  check the variable to see if the register was successful
        //  if it was, change logged in to true
        //  set auth = authtoken returned from result
        //  print out result ("Yay you are in!" or "Boo you are dumb")
        if(isError){
            System.out.println(authToken);
            notLoggedInHelp();
            notLoggedIn();

        } else {
            auth = authToken;
            System.out.println("Register successful!");
            isLoggedIn = true;
        }
    }

    private Boolean loggedIn() throws ResponseException{
        String input = scanner.nextLine();
        if(input.equals("1")){
            createGame();
        } else if(input.equals("2")) {
            listGames();
        } else if(input.equals("3")) {
            playGame();
        } else if(input.equals("4")) {
            observeGame();
        } else if(input.equals("5")) {
            logoutUser();
            return false;
        } else if(input.equals("6")) {
            loggedInHelp();
        } else {
            System.out.println("Not a valid option.\n");
        }
        return true;
    }

    private boolean isNumeric(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void createGame() throws ResponseException{
        System.out.println("Create a name for your Chessgame: ");
        String gameName = scanner.nextLine();
        while(gameName.isBlank()){
            System.out.println("Create a name for your Chessgame: ");
            gameName = scanner.nextLine();
        }
        // plug in the authToken given from the register/login
        String gameID = serverFacade.create(gameName, auth);
        // TODO call server facade create game
        //  next get response back and store in a variable
        //  check the variable to see if the create game was successful
        //  store gameID but don't print it out
        //  print out result ("Game successfully created" or "Game not created")
        if(!isNumeric(gameID)){
            System.out.println(gameID);
        } else {
            gameIDs.add(Integer.parseInt(gameID));
            System.out.println("Game successfully created!");
        }
    }

    private void listGames() throws ResponseException{
        boolean isError = false;

        // plug in the authToken given from the register/login
        String listString = serverFacade.list(auth, isError);
        if(isError){
            System.out.println(listString);
            loggedInHelp();
            loggedIn();
        }
        System.out.println("Here are all the available games: ");

        ListResult listResult = new Gson().fromJson(listString, ListResult.class);

        Collection<GameData> gameList = listResult.games();

        ArrayList<String> games = new ArrayList<>();
        for(GameData game : gameList){
            StringBuilder individualGameData = new StringBuilder();
            individualGameData.append(" " + game.whiteUsername() + ", ");
            individualGameData.append(game.blackUsername() + ", " + game.gameName());
            games.add(individualGameData.toString());
        }

        // TODO call server facade list game
        //  next get response back and store in a variable
        //  check the variable to see if the list game was successful
        //  store gameID but don't print it out
        //  print out result ("Games successfully listed" or "Games not listed")
        StringBuilder result = new StringBuilder();
        Gson gson = new Gson();
        for(int i = 0; i < games.size(); i++){
            result.append(i + 1 + ". " + gson.toJson(games.get(i))).append('\n');
            //gameIDs.add(i + 1);
        }
        System.out.println(result.toString());
    }

    private void playGame() throws ResponseException{
        listGames();
        Collection<String> inputGameIDs = new ArrayList<>();

        for(Integer i : gameIDs){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(i);
            inputGameIDs.add(stringBuilder.toString());
        }

        System.out.println("Pick a game you want to play: ");
        String gameID = scanner.nextLine();
        while(gameID.isBlank()){
            System.out.println("Pick a game you want to play: ");
            gameID = scanner.nextLine();
        }
        while(!inputGameIDs.contains(gameID) || !isNumeric(gameID)){
            System.out.println("Error: not a valid option.");
            System.out.println("Pick a game you want to play: ");
            gameID = scanner.nextLine();
        }
        Integer newID = 0;
        for(int ID : gameIDs) {
            if(Integer.parseInt(gameID) == ID){
                newID = ID;
                break;
            }
        }

        System.out.println("Choose what team you wish to play (White or Black): ");
        String playerColor = scanner.nextLine().toLowerCase();
        while(playerColor.isBlank()){
            System.out.println("Choose what team you wish to play (White or Black): ");
            playerColor = scanner.nextLine().toLowerCase();
        }
        while(!playerColor.equals("white") && !playerColor.equals("black")){
            System.out.println("Choose what team you wish to play (White or Black): ");
            playerColor = scanner.nextLine().toLowerCase();
        }
        // check to see if that teamcolor is taken or not.

        // plug in the authToken given from the register/login
        String joinMessage = serverFacade.join(auth, newID, playerColor);
        // TODO call server facade join game
        //  next get response back and store in a variable
        //  check the variable to see if the join game was successful
        //  print out result ("Game successfully joined" or "Join failed")

        // TODO: figure out gameIDs with associated games to figure out which game to display.
        //   for now, until the above is completed, just print out the board for an unspecified game. Fix this later.
        if(joinMessage.equals("join successful!")){
            System.out.println("Join successful!");
            ChessBoard board = chessPiecePositions();
            DrawChessboard drawChessboard = new DrawChessboard(board);
            drawChessboard.run();
        } else {
            System.out.println(joinMessage);
            loggedInHelp();
            loggedIn();
        }
    }

    private void observeGame(){
        // print out the list of games with associated numbers starting at 1 (independent of gameID)
        System.out.println("Choose a game to observe: ");
        // note: gameplay will not be implemented until Phase 6. For now, just display the ChessBoard
        String gameName = scanner.nextLine();
        // note: no calling the ServerFacade here. The Client keeps track of which number is associated with which game
        // may want to create a hashset that keeps track of server gameIDs and the ui gameIDs
        while(gameName.isBlank()){
            System.out.println("Choose a game to play: ");
            // print out the list of games with associated numbers starting at 1 (independent of gameID)
            gameName = scanner.nextLine();
        }
        // TODO: figure out gameIDs with associated games to figure out which game to display.
        //   for now, until the above is completed, just print out the board for an unspecified game. Fix this later.
        ChessBoard board = chessPiecePositions();
        DrawChessboard drawChessboard = new DrawChessboard(board);
        drawChessboard.run();
    }

    private void logoutUser() throws ResponseException {

        String logoutMessage = serverFacade.logout(auth);
        // TODO call server facade logout
        //  next get response back and store in a variable
        //  check the variable to see if the logout was successful
        //  reset auth = ""
        //  print out result ("logout successful!" or "logout failed")
        //  if an exception is thrown, print out the error message.
        if(logoutMessage.equals("logout successful!")){
            System.out.println("logout successful!");
            auth = null;
            isLoggedIn = false;
            notLoggedInHelp();
        } else {
            System.out.println(logoutMessage);
            loggedInHelp();
            loggedIn();
        }
    }

    private void loggedInHelp(){
        System.out.println("Welcome to Chess!");
        System.out.println("Choose an option: ");
        System.out.println("  1. Create Game");
        System.out.println("  2. List Games");
        System.out.println("  3. Play Game");
        System.out.println("  4. Observe Game");
        System.out.println("  5. Logout");
        System.out.println("  6. Help");
    }

    // create matrix for chesspiece locations
    public ChessBoard chessPiecePositions() {
        // note: this may be a temporary solution, as it may or may not be compatible with Phase 6
        // for now though, it works fine
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        return board;
    }
}
