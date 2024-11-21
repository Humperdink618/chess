package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import result.ListResult;
import ui.serverfacade.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.*;

public class ChessClient implements ServerMessageObserver {
    // These are variables which you will need for multiple functions
    private Scanner scanner = new Scanner(System.in);
    private String auth = null;
    private final String serverURL;
    private final ServerFacade serverFacade;
    private Boolean isLoggedIn = false;
    private Boolean isPlayingGame = false;
    private Collection<Integer> gameIDs = new HashSet<>();
    private Collection<GameData> gameDataList = new HashSet<>();
    // Optional: This is just for fun!
    private Boolean isSarcasticText = false;
    private int counter = 0;
    private Boolean bootUser = false;

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
         if(bootUser) {
             return true;
         }
        String input = scanner.nextLine();
        if(input.equals("1")){
            loginUser();
        } else if(input.equals("2")) {
            if(isSarcasticText){
                System.out.println("Oh thank goodness! Someone new.");
                System.out.println("Let's hope you're not as cheeky as the last person who tried to log in. Have fun!");
                isSarcasticText = false;
                counter = 0;
                registerUser();
            } else {
                registerUser();
            }
        } else if(input.equals("3")) {
            counter = 0;
            isSarcasticText = false;
            return true;
        } else if(input.equals("4")) {
            if(isSarcasticText){
                notLoggedInHelpSarcastic();
                notLoggedInHelp();
            } else {
                notLoggedInHelp();
            }
        } else {
            if(isSarcasticText){
                System.out.println("Not a valid option, idiot!\n");
            } else {
                System.out.println("Not a valid option.\n");
            }
            notLoggedInHelp();
        }
        return false;
    }

    private void notLoggedInHelpSarcastic(){
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

    private void notLoggedInHelp(){
        System.out.println("Choose an option: ");
        System.out.println("  1. Login");
        System.out.println("  2. Register");
        System.out.println("  3. Quit");
        System.out.println("  4. Help");
    }

    private void loginUser() throws ResponseException{
        String inputUserName = null;
        String inputPassword = null;
        if(isSarcasticText){
            System.out.println("Please enter your username or whatever. And PLEASE get it right this time!: ");
            inputUserName = scanner.nextLine();
            while(inputUserName.isBlank()) {
                // isBlank() returns true if input is empty string or only composed of whitespace characters.
                System.out.println("Error: not a valid option, stupid.");
                System.out.println("Please enter the CORRECT username. We don't have all day: ");
                inputUserName = scanner.nextLine();
            }
            System.out.println("Enter your Password or whatever. And please don't mess this up: ");
            inputPassword = scanner.nextLine();
            while(inputPassword.isBlank()) {
                System.out.println("Error: not a valid option, stupid.");
                System.out.println("Please enter the CORRECT password: ");
                inputPassword = scanner.nextLine();
            }
        } else {
            System.out.println("Please enter your username: ");
            inputUserName = scanner.nextLine();
            while (inputUserName.isBlank()) {
                // isBlank() returns true if input is empty string or only composed of whitespace characters.
                System.out.println("Error: not a valid option.");
                System.out.println("Please enter your username: ");
                inputUserName = scanner.nextLine();
            }
            System.out.println("Enter your Password: ");
            inputPassword = scanner.nextLine();
            while (inputPassword.isBlank()) {
                System.out.println("Error: not a valid option.");
                System.out.println("Enter your Password: ");
                inputPassword = scanner.nextLine();
            }
        }
        String authToken = serverFacade.login(inputUserName,inputPassword);

        if(authToken.contains("message")){
            HashMap errorMessageMap = new Gson().fromJson(authToken, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            if(errorMessage.equals("Error: unauthorized")) {
                System.out.println("Error: invalid username or password.");
            } else {
                System.out.println(errorMessage);
            };
            System.out.println("If you want to try again, Enter 1. If you want to return to the main menu, Enter 2.");
            String inputAnswer = scanner.nextLine();
            if(inputAnswer.equals("1")){
                loginUser();
            } else if(inputAnswer.equals("2")){
                notLoggedInHelp();
                notLoggedIn();
            } else {
                if(isSarcasticText){
                    if(counter == 10){
                        System.out.println("You know what? I'm done here!");
                        System.out.println("If you're not going to take this seriously, " +
                                "then you don't deserve to play!");
                        System.out.println("I am making an executive decision here, and closing this session myself.");
                        System.out.println("Come back when you're ready to behave and actually take this seriously!");
                        bootUser = true;
                        notLoggedIn();
                    } else {
                        System.out.println("Ok, this is getting ridiculous. Just Enter 1 or 2 for crying out loud!");
                        System.out.println("It's not rocket science! It's just tapping two keys on a keyboard.");
                        System.out.println("It's so simple! Heck, a BABY could do it! Please stop wasting my time!");
                        counter += 1;
                        notLoggedInHelp();
                        notLoggedIn();
                    }
                } else {
                    System.out.println("Oh, so you think you're funny, eh? " +
                            "Well, I guess I'll make the decision FOR you...");
                    counter += 1;
                    if (counter >= 3) {
                        isSarcasticText = true;
                    }
                    notLoggedInHelp();
                    notLoggedIn();
                }
            }

        } else {
            if(!isSarcasticText){
                counter = 0;
            }
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
            if(errorMessage.equals("Error: bad request")){
                System.out.println("Error: Failed to register user due to invalid username, password, or email.");
            } else if(errorMessage.equals("Error: already taken")){
                System.out.println(errorMessage);
            } else {
                System.out.println(errorMessage);
            }
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
            isSarcasticText = false;
            counter = 0;
            return false;
        } else if(input.equals("6")) {
            loggedInHelp();
/*       } else if(input.equals("7")) { // DELETE THIS LINE
            clearDB();
*/
            /*
        } else if(input.equals("8")) { // DELETE THIS LINE
            highlightLegalMoves();
*/

        } else {
            if(isSarcasticText){
                System.out.println("Not a valid option, idiot!\n");
            } else {
                System.out.println("Not a valid option.\n");
            }
        }
        return true;
    }

    private void createGame() throws ResponseException{
        System.out.println("Create a name for your Chessgame: ");
        String gameName = scanner.nextLine();
        while(gameName.isBlank()){
            if(isSarcasticText){
                System.out.println("Um, you actually need to write something here. It's NOT hard. Try again:");
                System.out.println("Create a name for your Chessgame: ");
                gameName = scanner.nextLine();
            } else {
                System.out.println("Error: not a valid option.");
                System.out.println("Create a name for your Chessgame: ");
                gameName = scanner.nextLine();
            }
        }
        // plug in the authToken given from the register/login
        String gameID = serverFacade.create(gameName, auth);

        if(!isNumeric(gameID)){
            HashMap errorMessageMap = new Gson().fromJson(gameID, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            if(errorMessage.equals("Error: unauthorized")) {
                System.out.println(errorMessage);
            } else if(errorMessage.equals("Error: bad request")){
                System.out.println("Error: Failed to create game due to poor user input.");
            } else {
                System.out.println(errorMessage);
            }
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
            if(isSarcasticText){
                System.out.println("Here are all the available games, or whatever: ");
            } else {
                System.out.println("Here are all the available games: ");
            }
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
        // TODO: make it so that it only prints one side of the board depending on the teamcolor of the player
        //  (observers will view it from White's perspective by default)
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
            //ChessBoard board = chessPiecePositions();
//            ChessGame chessGame = chessPiecePositions();
//            DrawChessboard drawChessboard = new DrawChessboard(chessGame, 0);
//            drawChessboard.run();
            // websocketCommunicator does this for you
        } else {
            HashMap errorMessageMap = new Gson().fromJson(joinMessage, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            if(errorMessage.equals("Error: unauthorized")) {
                System.out.println(errorMessage);
            } else if(errorMessage.equals("Error: bad request")){
                System.out.println("Error: Join failed due to poor user input.");
            } else if(errorMessage.equals("Error: already taken")){
                System.out.println(errorMessage);
            } else {
                System.out.println(errorMessage);
            }
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
        // TODO: make it so that it only prints one side of the board depending on the teamcolor of the player
        //  (observers will view it from White's perspective by default)
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
        //ChessBoard board = chessPiecePositions();
//        ChessGame chessGame = new ChessGame();
//        DrawChessboard drawChessboard = new DrawChessboard(chessGame, 0);
//        drawChessboard.run();
        // websocketCommunicator should print it for you
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
            if(isSarcasticText){
                System.out.println("Logout successful! Good riddance...");
                auth = null;
                isLoggedIn = false;

                notLoggedInHelp();
            }
            System.out.println("logout successful!");
            auth = null;
            isLoggedIn = false;
            notLoggedInHelp();
        } else {
            HashMap errorMessageMap = new Gson().fromJson(logoutMessage, HashMap.class);
            String errorMessage = errorMessageMap.get("message").toString();
            if(errorMessage.equals("Error: unauthorized")) {
                System.out.println(errorMessage);
            }
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

    private Boolean gameMenu() throws ResponseException{
        String input = scanner.nextLine();
        if(input.equals("1")){
            redrawChessBoard();
        } else if(input.equals("2")) {
            makeMove();
        } else if(input.equals("3")) {
            highlightLegalMoves();
        } else if(input.equals("4")) {
            resign();
        } else if(input.equals("5")) {
            leave();
            return false;
        } else if(input.equals("6")) {
            gamePlayHelp();
            gameMenu(); // possibly may not need this line
        } else {
            System.out.println("Not a valid option.\n");
            displayGamePlayMenu();
        }
        return true;
    }

    private void displayGamePlayMenu(){
        System.out.println("Choose an option: ");
        System.out.println("  1. Redraw Chess Board");
        System.out.println("  2. Make Move");
        System.out.println("  3. Highlight Legal Moves");
        System.out.println("  4. Resign");
        System.out.println("  5. Leave");
        System.out.println("  6. Help");
    }

    private void redrawChessBoard(){
        // redraws the current chessboard
        // TODO: not implemented
    }

    private void makeMove(){
        // makes a move on the ChessBoard during a game
        // TODO: not implemented
    }

    private void highlightLegalMoves() throws ResponseException{
        // highlights all legal moves a chesspiece can make on a ChessBoard during a game
        // TODO: make it so that it only prints one side of the board depending on the teamcolor of the player
        //  (observers will view it from White's perspective by default)
        System.out.println("Enter the piece's position: ");
        String chessPos = scanner.nextLine();
        // TODO: check this method again once I've done all the websocket stuff and implemented a way that displays
        //  only one side of the board at a time. Will need to see if this still works for other chesspieces in other
        //  positions on the board (currently, this only checks Pawns and Knights, as no other piece can move from the
        //  default position.

        char[] inputCharPos = chessPos.toCharArray();
        for(int i = 0; i < inputCharPos.length; i++){
            if(!Character.isLetter(inputCharPos[0])) {
                System.out.println("Error: Invalid position.");
                // for testing purposes ONLY. Replace these values with the values from the gamePlayMenu later
                displayGamePlayMenu();
                gameMenu();
                //loggedInHelp();
                //loggedIn();
            } else if(!Character.isDigit(inputCharPos[1])){
                System.out.println("Error: Invalid position.");
                // for testing purposes ONLY. Replace these values with the values from the gamePlayMenu later
                displayGamePlayMenu();
                gameMenu();
                //loggedInHelp();
                //loggedIn();
            } else if(inputCharPos.length > 2) {
                System.out.println("Error: Invalid position.");
                // for testing purposes ONLY. Replace these values with the values from the gamePlayMenu later
                displayGamePlayMenu();
                gameMenu();
                //loggedInHelp();
                //loggedIn();
            }
        }

        int x = 0;
        int y = 0;
        for(char c : inputCharPos){
            if(Character.isLetter(c)){
                x = c - 'a' + 1;
            } else if(Character.isDigit(c)){
                y = Character.getNumericValue(c);
            } else {
                System.out.println("Error: Invalid position.");
                // for testing purposes ONLY. Replace these values with the values from the gamePlayMenu later
                loggedInHelp();
                loggedIn();
                //displayGamePlayMenu();
                //gameMenu();

            }
        }

        ChessPosition inputPos = new ChessPosition(y, x);
        ChessBoard board = chessPiecePositions().getBoard();
        Collection<ChessPosition> chessPositions = board.getChessPositions();
        for(ChessPosition position : chessPositions){
            if(position.getRow() == inputPos.getRow() && position.getColumn() == inputPos.getColumn()){
                // TODO: make this code grab the most up-to-date chessboard/chessgame
                DrawChessboard drawChessboard = new DrawChessboard(chessPiecePositions(), 1);
                drawChessboard.runHighlight(inputPos);
            }
        }

    }

    private void resign(){
        // prompts the user to confirm they want to resign.
        // If they do, the user forfeits the game and the game is over.
        // Does not cause the user to leave the game.
        // TODO: not implemented
    }

    private void leave() throws ResponseException{
        // removes the user from the game (whether they are playing or observing the game).
        // The client transitions back to the Post-Login UI.
        // TODO: not implemented
        isPlayingGame = false;
        loggedInHelp();
        loggedIn();
    }

    private void gamePlayHelp() throws ResponseException{
        System.out.println("Enter 1 to redraw the game board.");
        System.out.println("Enter 2 to make a move.");
        System.out.println("  (Note: This option is only valid for users actually playing the game. If you are just " +
                "observing, you may NOT use this option.");
        System.out.println("Enter 3 to highlight all legal moves for a specific chess piece.");
        System.out.println("Enter 4 to forfeit, ending the game.");
        System.out.println("  (Note: you may not forfeit if you are merely observing a game and not playing it.");
        System.out.println("   Also, resigning from the game does not cause the user to leave the game, " +
                " whether you are playing or merely obseving.)");
        System.out.println("Enter 5 to leave the game.");
        System.out.println(" (Note: This is not the same thing as resigning from a game you are playing.)");
        System.out.println("Enter 6 to see this message again.\n");
        displayGamePlayMenu();
    }

    // note: only for testing purposes. Delete afterwards

/*
    private void clearDB() throws ResponseException{

*/

        // ADMIN ONLY!
/*        serverFacade.clear();

        System.out.println("CLEARED");
*/

/*
        isLoggedIn = false;

        notLoggedInHelp();
*/

  //  }
    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) serverMessage));
            // create a subclass
            case ERROR -> displayError(((ErrorMessage) serverMessage));
            case LOAD_GAME -> loadGame(((LoadGameMessage) serverMessage).getGame());
        }
    }


    private void loadGame(ChessGame game) {
        DrawChessboard drawChessboard = new DrawChessboard(game, 0);
        drawChessboard.run();
    }

    private void displayNotification(NotificationMessage serverMessage) {
        System.out.println(serverMessage.getMessage());
    }

    public void displayError(ErrorMessage errorMessage){
        System.out.println(errorMessage.getErrorMessage());
    }

    // create matrix for chesspiece locations
    //public ChessBoard chessPiecePositions() {
    public ChessGame chessPiecePositions() {
        // note: this may be a temporary solution, as it may or may not be compatible with Phase 6
        // for now though, it works fine
        ChessGame chessGame = new ChessGame(); // may change this by inputting a parameter
        //ChessBoard board = chessGame.getBoard();
        //ChessBoard board = new ChessBoard();
        //board.resetBoard();
        //return board;
        return chessGame;
    }
}