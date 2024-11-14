package ui;

import chess.ChessBoard;
import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import result.ListResult;
import ui.serverfacade.ServerFacade;

import java.util.*;

public class ChessClient {
    // These are variables which you will need for multiple functions
    private Scanner scanner = new Scanner(System.in);
    private String auth = null;
    private final String serverURL;
    private final ServerFacade serverFacade;
    private Boolean isLoggedIn = false;
    private Collection<Integer> gameIDs = new HashSet<>();
    private Collection<GameData> gameDataList = new HashSet<>();

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
            System.out.println("Error: not a valid option.");
            System.out.println("Please enter your username: ");
            inputUserName = scanner.nextLine();
        }
        System.out.println("Enter your Password: ");
        String inputPassword = scanner.nextLine();
        while(inputPassword.isBlank()) {
            System.out.println("Error: not a valid option.");
            System.out.println("Enter your Password: ");
            inputPassword = scanner.nextLine();
        }
        String authToken = serverFacade.login(inputUserName,inputPassword);

        if(authToken.contains("message")){
            HashMap errorMessageMap = new Gson().fromJson(authToken, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            System.out.println(errorMessage);
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
            System.out.println("Error: not a valid option.");
            System.out.println("Please create a valid username: ");
            inputUserName = scanner.nextLine();
        }
        System.out.println("Create your Password: ");
        String inputPassword = scanner.nextLine();
        while(inputPassword.isBlank()) {
            System.out.println("Error: not a valid option.");
            System.out.println("Create your Password: ");
            inputPassword = scanner.nextLine();
        }
        System.out.println("Please enter your Email: ");
        String inputEmail = scanner.nextLine();
        while(inputEmail.isBlank()){
            System.out.println("Error: not a valid option.");
            System.out.println("Please enter your Email: ");
            inputEmail = scanner.nextLine();
        }
        String authToken = serverFacade.register(inputUserName,inputPassword, inputEmail);
        if(authToken.contains("message")){
            HashMap errorMessageMap = new Gson().fromJson(authToken, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            System.out.println(errorMessage);
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
/*       } else if(input.equals("7")) { // DELETE THIS LINE
            clearDB();
*/
        } else {
            System.out.println("Not a valid option.\n");
        }
        return true;
    }

    private void createGame() throws ResponseException{
        System.out.println("Create a name for your Chessgame: ");
        String gameName = scanner.nextLine();
        while(gameName.isBlank()){
            System.out.println("Error: not a valid option.");
            System.out.println("Create a name for your Chessgame: ");
            gameName = scanner.nextLine();
        }
        // plug in the authToken given from the register/login
        String gameID = serverFacade.create(gameName, auth);

        if(!isNumeric(gameID)){
            HashMap errorMessageMap = new Gson().fromJson(gameID, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            System.out.println(errorMessage);
            loggedInHelp();
            loggedIn();
        } else {
            //gameIDs.add(Integer.parseInt(gameID));
            System.out.println("Game successfully created!");
            loggedInHelp();
            loggedIn();
        }
    }

    private void listGames() throws ResponseException{
        // call server facade list game
        // plug in the authToken given from the register/login
        String listString = serverFacade.list(auth);
        //  next get response back and store in a variable

        if(listString.contains("message")){
            HashMap errorMessageMap = new Gson().fromJson(listString, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            System.out.println(errorMessage);
            loggedInHelp();
            loggedIn();
        }

        ListResult listResult = new Gson().fromJson(listString, ListResult.class);

        Collection<GameData> gameList = listResult.games();
        //  check the variable to see if the list game was successful
        if(gameList == null || gameList.isEmpty()){
            System.out.println("No available games to display.");
            loggedInHelp();
            loggedIn();
        } else {
            System.out.println("Here are all the available games: ");
            HashMap<Integer, String> gameMap = new HashMap<>();
            for (GameData game : gameList) {
                StringBuilder individualGameData = new StringBuilder();
                individualGameData.append(" " + game.whiteUsername() + ", ");
                individualGameData.append(game.blackUsername() + ", " + game.gameName());
                //  store gameID but don't print it out
                gameIDs.add(game.gameID());
                gameDataList.add(game);
                gameMap.put(game.gameID(), individualGameData.toString());
            }
            //  print out result
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < gameMap.size(); i++) {
                result.append(i + 1 + ". " + gameMap.get(i+1)).append('\n');
            }
            System.out.println(result.toString());
        }
    }

    private void playGame() throws ResponseException{
        listGames();
        System.out.println("Pick a game you want to play: ");
        String gameID = scanner.nextLine();
        while(gameID.isBlank() || !isNumeric(gameID)){
            System.out.println("Error: not a valid option.");
            System.out.println("Pick a game you want to play: ");
            gameID = scanner.nextLine();
        }
        gameID = checkIfValidGameIDPlay(gameID);
        Integer newID = 0;
        for(int id : gameIDs) {
            if(Integer.parseInt(gameID) == id){
                newID = getGameIDFromGameDataList(id, gameID, newID);
                if(newID != 0){
                    break;
                }
            }
        }

        System.out.println("Choose what team you wish to play (White or Black): ");
        String playerColor = scanner.nextLine().toUpperCase();
        while(playerColor.isBlank()){
            System.out.println("Error: not a valid option.");
            System.out.println("Choose what team you wish to play (White or Black): ");
            playerColor = scanner.nextLine().toUpperCase();
        }
        while(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
            System.out.println("Error: not a valid option.");
            System.out.println("Choose what team you wish to play (White or Black): ");
            playerColor = scanner.nextLine().toUpperCase();
        }
        // check to see if that teamcolor is taken or not.

        // plug in the authToken given from the register/login
        String joinMessage = serverFacade.join(auth, newID, playerColor);
        // TODO: figure out gameIDs with associated games to figure out which game to display.
        //   for now, until the above is completed, just print out the board for an unspecified game. Fix this later.
        if(joinMessage.equals("join successful!")){
            System.out.println("Join successful!");
            ChessBoard board = chessPiecePositions();
            DrawChessboard drawChessboard = new DrawChessboard(board);
            drawChessboard.run();
        } else {
            HashMap errorMessageMap = new Gson().fromJson(joinMessage, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            System.out.println(errorMessage);
            loggedInHelp();
            loggedIn();
        }
    }

    private Integer getGameIDFromGameDataList(int id, String gameID, Integer newID) {
        for(GameData game : gameDataList){
            if(Integer.parseInt(gameID) == game.gameID()){
                newID = id;
                return newID;
            }
        }
        return 0;
    }

    private String checkIfValidGameIDPlay(String gameID) {
        while(!gameIDs.contains(Integer.parseInt(gameID)) || !isNumeric(gameID)){
            System.out.println("Error: not a valid option.");
            System.out.println("Pick a game you want to play: ");
            gameID = scanner.nextLine();
        }
        return gameID;
    }

    private String checkIfValidGameIDObserve(String gameID) {
        while(!gameIDs.contains(Integer.parseInt(gameID)) || !isNumeric(gameID)){
            System.out.println("Error: not a valid option.");
            System.out.println("Pick a game you want to observe: ");
            gameID = scanner.nextLine();
        }
        return gameID;
    }

    private void observeGame() throws ResponseException{
        // print out the list of games with associated numbers starting at 1 (independent of gameID)
        listGames();
        System.out.println("Choose a game to observe: ");
        // note: gameplay will not be implemented until Phase 6. For now, just display the ChessBoard
        String gameName = scanner.nextLine();
        // note: no calling the ServerFacade here. The Client keeps track of which number is associated with which game
        // may want to create a hashset that keeps track of server gameIDs and the ui gameIDs
        while(gameName.isBlank() || !isNumeric(gameName)){
            System.out.println("Error: not a valid option.");
            System.out.println("Choose a game to observe: ");
            // print out the list of games with associated numbers starting at 1 (independent of gameID)
            gameName = scanner.nextLine();
        }
        gameName = checkIfValidGameIDObserve(gameName);

        // TODO: figure out gameIDs with associated games to figure out which game to display.
        //   for now, until the above is completed, just print out the board for an unspecified game. Fix this later.
        ChessBoard board = chessPiecePositions();
        DrawChessboard drawChessboard = new DrawChessboard(board);
        drawChessboard.run();
    }

    // note: I am putting this here so that it can be used by both my ChessClient AND my ServerFacadeTests
    public boolean isNumeric(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void logoutUser() throws ResponseException {

        String logoutMessage = serverFacade.logout(auth);

        if(logoutMessage.equals("logout successful!")){
            System.out.println("logout successful!");
            auth = null;
            isLoggedIn = false;
            notLoggedInHelp();
        } else {
            HashMap errorMessageMap = new Gson().fromJson(logoutMessage, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            System.out.println(errorMessage);
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
    // note: only for testing purposes. Delete afterwards

    /*
    private void clearDB() throws ResponseException{
     */


        // ADMIN ONLY!
        /*serverFacade.clear();

        System.out.println("CLEARED");
         */

    /*
        isLoggedIn = false;

        notLoggedInHelp();
     */

    //}

    // create matrix for chesspiece locations
    public ChessBoard chessPiecePositions() {
        // note: this may be a temporary solution, as it may or may not be compatible with Phase 6
        // for now though, it works fine
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return board;
    }
}