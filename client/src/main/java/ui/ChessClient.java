package ui;

import chess.*;
import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;
import result.ListResult;
import ui.serverfacade.ServerFacade;
import ui.serverfacade.WebsocketCommunicator;
import websocket.messages.*;

import java.util.*;

import static ui.EscapeSequences.*;

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
    private ChessGame chessGame;
    private String playerColorClient = null;
    private Integer gameID = null;
    private Boolean isObserver = false;

    //TODO: find some way to shorten this class
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
                if(isPlayingGame){
                    returnToGameMenu(getPlayerColorClient(), getGameID());
                } else {
                    loggedInHelp();
                    loggedIn();
                }

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

    public String getPlayerColorClient() {
        return playerColorClient;
    }

    public void setPlayerColorClient(String playerColorClient) {
        this.playerColorClient = playerColorClient;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
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
            isPlayingGame = false;
            isObserver = false;
            return false;
        } else if(input.equals("6")) {
            loggedInHelp();
  /*     } else if(input.equals("7")) { // DELETE THIS LINE
            clearDB();
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
        System.out.println("Create a name for your Chess game: ");
        String gameName = scanner.nextLine();
        while(gameName.isBlank()){
            if(isSarcasticText){
                System.out.println("Um, you actually need to write something here. It's NOT hard. Try again:");
                System.out.println("Create a name for your Chess game: ");
                gameName = scanner.nextLine();
            } else {
                System.out.println("Error: not a valid option.");
                System.out.println("Create a name for your Chess game: ");
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

    private void playGame() throws ResponseException {

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
        // check to see if that team color is taken or not.
        // plug in the authToken given from the register/login
        String joinMessage = serverFacade.join(auth, newID, playerColor);

        if(joinMessage.equals("join successful!")){
            System.out.println("Join successful!");
            try {
                WebsocketCommunicator ws = new WebsocketCommunicator(this);
                ws.enterGamePlayMode(auth, newID);
                isPlayingGame = true;
                setGameID(newID);
                setPlayerColorClient(playerColor);
            } catch (Exception e) {
                //displayError(new ErrorMessage(e.getMessage()));
                displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
            }
            returnToGameMenu(playerColor, newID);
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
        System.out.println("Choose a game to observe: ");
        String gameName = scanner.nextLine();
        // note: no calling the ServerFacade here. The Client keeps track of which number is associated with which game
        while(gameName.isBlank() || !isNumeric(gameName)){
            System.out.println("Error: not a valid option.");
            System.out.println("Choose a game to observe: ");
            // print out the list of games with associated numbers starting at 1 (independent of gameID)
            gameName = scanner.nextLine();
        }
        String gameIDString = checkIfValidGameIDObserve(gameName);
        int gameID = Integer.parseInt(gameIDString);
        try {
            WebsocketCommunicator ws = new WebsocketCommunicator(this);
            ws.enterGamePlayMode(auth, gameID);
            isPlayingGame = true;
            isObserver = true;
            setGameID(gameID);
            setPlayerColorClient("WHITE");
            System.out.println("You are now observing the game.");
        } catch (Exception e) {
            //displayError(new ErrorMessage(e.getMessage()));
            displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
        }
        returnToGameMenu("WHITE", gameID);
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

    // note: only for testing purposes. Delete afterward
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
 //   }

    private Boolean gameMenu(String playerColor, int gameID) throws ResponseException{
        String input = scanner.nextLine();
        if(input.equals("1")){
            redrawChessBoard(playerColor, gameID);
        } else if(input.equals("2")) {
            makeMove(playerColor, gameID);
        } else if(input.equals("3")) {
            highlightLegalMoves(playerColor, gameID);
        } else if(input.equals("4")) {
            resign(playerColor, gameID);
        } else if(input.equals("5")) {
            leave(gameID);
            return false;
        } else if(input.equals("6")) {
            gamePlayHelp();
            gameMenu(playerColor, gameID); // possibly may not need this line
        } else {
            System.out.println("Not a valid option.\n");
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

    private void redrawChessBoard(String playerColor, int gameID) throws ResponseException {
        // redraws the current chessboard
        ChessGame game = getChessGame();
        DrawChessboard drawChessboard = new DrawChessboard(game, playerColor);
        drawChessboard.run();
        returnToGameMenu(playerColor, gameID);
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }
    // TODO: try to see if I can shorten this method (if possible!)
    private void makeMove(String playerColor, int gameID) throws ResponseException{
        // makes a move on the ChessBoard during a game
        if(isObserver){
            System.out.println("Error: you can't move a piece if you are just observing and not actually " +
                    "playing the game.");
            returnToGameMenu(playerColor, gameID);
        }
        System.out.println("Enter the piece's start position in the form b2 (a letter from 'a' to 'h' " +
                "followed by a number from 1 to 8): ");
        String inputStartPos = scanner.nextLine().toLowerCase();

        while(inputStartPos.isBlank()) {
            System.out.println("Error: not a valid option.");
            System.out.println("Enter the piece's start position in the form b2 (a letter from 'a' to 'h' " +
                    "followed by a number from 1 to 8): ");
            inputStartPos = scanner.nextLine().toLowerCase();
        }

        String isInvalidPos = "Error: Invalid position.";
        char[] inputCharStartPos = inputStartPos.toCharArray();
        checkIfCharArrayIsValidInput(playerColor, gameID, inputCharStartPos, isInvalidPos);

        int i = 0;
        int j = 0;

        for(char c : inputCharStartPos){
            if(Character.isLetter(c)){
                i = c - 'a' + 1;
            } else if(Character.isDigit(c)){
                j = Character.getNumericValue(c);
            } else {
                returnToMenuBCBadPos(playerColor, gameID, isInvalidPos);
            }
        }

        checkIfRowAndColAreRightSize(i < 1 || i > 8 || j < 1 || j > 8, playerColor, gameID, isInvalidPos);

        ChessPosition startPos = new ChessPosition(j, i);

        System.out.println("Where would you like to move this piece? (Enter the piece's end position in the form b2 " +
                "(a letter from 'a' to 'h' followed by a number from 1 to 8): ");
        String inputEndPos = scanner.nextLine().toLowerCase();

        while(inputEndPos.isBlank()) {
            System.out.println("Error: not a valid option.");
            System.out.println("Where would you like to move this piece? (Enter the piece's end position in the" +
                    " form b2 (a letter from 'a' to 'h' followed by a number from 1 to 8): ");
            inputEndPos = scanner.nextLine().toLowerCase();
        }

        char[] inputCharEndPos = inputEndPos.toCharArray();
        checkIfCharArrayIsValidInput(playerColor, gameID, inputCharEndPos, isInvalidPos);

        int z = 0;
        int k = 0;

        for(char c : inputCharEndPos){
            if(Character.isLetter(c)){
                z = c - 'a' + 1;
            } else if(Character.isDigit(c)){
                k = Character.getNumericValue(c);
            } else {
                returnToMenuBCBadPos(playerColor, gameID, isInvalidPos);
            }
        }

        checkIfRowAndColAreRightSize(z < 1 || z > 8 || k < 1 || k > 8, playerColor, gameID, isInvalidPos);

        ChessPosition endPos = new ChessPosition(k, z);

        ChessBoard board = chessPiecePositions().getBoard();
        ChessPiece chessPiece = board.getPiece(startPos);
        Boolean canPromote = false;
        if(chessPiece == null){
            returnToMenuBCBadPos(playerColor, gameID, isInvalidPos);
        } else if(chessPiece.getPieceType() == ChessPiece.PieceType.PAWN){
            if(chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                if(endPos.getRow() == 8){
                    canPromote = true;
                } else {
                    canPromote = false;
                }
            } else if(chessPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
                if(endPos.getRow() == 1){
                    canPromote = true;
                } else {
                    canPromote = false;
                }
            }
        }
        ChessPiece promotionPiece = null;
        if(canPromote) {
            System.out.println("Enter the piece type you want to promote your pawn to (can be any piece except for " +
                    "KING and PAWN): ");
            String inputPromotionPiece = scanner.nextLine().toUpperCase();
            while(inputPromotionPiece.isBlank()) {
                System.out.println("Error: if you are moving a pawn to the opposite side of the board, it has to be " +
                        "promoted to something.");
                System.out.println("Enter the piece type you want to promote your pawn to (can be any piece except" +
                        " for KING and PAWN): ");
                inputPromotionPiece = scanner.nextLine().toUpperCase();
            }
            while (inputPromotionPiece.equals("KING")){
                System.out.println("Ok, who's the wise guy who tried to promote his pawn to a king, eh?");
                System.out.println("While I appreciate the attempt to sow a little bit of anarchy into the mix here, " +
                        "unfortunately, promoting a pawn to a king is against the rules.");
                System.out.println("In other words... \n");
                System.out.println("Error: not a valid promotion.");
                System.out.println("Enter the piece type you want to promote your pawn to (can be any piece except" +
                        " for KING and PAWN): ");
                inputPromotionPiece = scanner.nextLine().toUpperCase();
            }
            while(!inputPromotionPiece.equals("QUEEN") &&
                    !inputPromotionPiece.equals("ROOK") &&
                    !inputPromotionPiece.equals("KNIGHT") &&
                    !inputPromotionPiece.equals("BISHOP") &&
                    !inputPromotionPiece.equals("KING")){

                System.out.println("Error: not a valid promotion.");
                System.out.println("Enter the piece type you want to promote your pawn to (can be any piece except" +
                        " for KING and PAWN): ");
                inputPromotionPiece = scanner.nextLine().toUpperCase();
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
        ChessMove move = null;
        if(promotionPiece == null) {
            move = new ChessMove(startPos, endPos, null);

        } else {
            move = new ChessMove(startPos, endPos, promotionPiece.getPieceType());
        }
        if(move == null){
            System.out.println("Error: invalid move");
            returnToGameMenu(playerColor, gameID);
        }

        try {
            WebsocketCommunicator ws = new WebsocketCommunicator(this);
            ws.clientMakeMove(auth, gameID, move);

        } catch (Exception e) {
            //displayError(new ErrorMessage(e.getMessage()));
            displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
        }

        returnToGameMenu(playerColor, gameID);
    }

    private void checkIfRowAndColAreRightSize(boolean x, String playerColor, int gameID, String isInvalidPos) throws ResponseException {
        if (x) {
            returnToMenuBCBadPos(playerColor, gameID, isInvalidPos);
        }
    }

    private void returnToGameMenu(String playerColor, int gameID) throws ResponseException {
        displayGamePlayMenu();
        gameMenu(playerColor, gameID);
    }

    private void checkIfCharArrayIsValidInput(
            String playerColor,
            int gameID,
            char[] inputCharPos,
            String isInvalidPos) throws ResponseException {

        for(int i = 0; i < inputCharPos.length; i++){
            if(!Character.isLetter(inputCharPos[0])) {
                returnToMenuBCBadPos(playerColor, gameID, isInvalidPos);

            } else if(!Character.isDigit(inputCharPos[1])) {
                returnToMenuBCBadPos(playerColor, gameID, isInvalidPos);

            } else checkIfRowAndColAreRightSize(inputCharPos.length > 2, playerColor, gameID, isInvalidPos);
        }
    }

    private void highlightLegalMoves(String playerColor, int gameID) throws ResponseException{
        // highlights all legal moves a chess piece can make on a ChessBoard during a game

        System.out.println("Enter the piece's position in the form b2 (a letter from 'a' to 'h' " +
        "followed by a number from 1 to 8): ");
        String chessPos = scanner.nextLine().toLowerCase();

        while(chessPos.isBlank()) {
            System.out.println("Error: not a valid option.");
            System.out.println("Enter the piece's position in the form b2 (a letter from 'a' to 'h' " +
                    "followed by a number from 1 to 8): ");
            chessPos = scanner.nextLine().toLowerCase();
        }
        String isInvalidPos = "Error: Invalid position.";
        char[] inputCharPos = chessPos.toCharArray();
        checkIfCharArrayIsValidInput(playerColor, gameID, inputCharPos, isInvalidPos);

        int x = 0;
        int y = 0;
        for(char c : inputCharPos){
            if(Character.isLetter(c)){
                x = c - 'a' + 1;
            } else if(Character.isDigit(c)){
                y = Character.getNumericValue(c);
            } else {
                returnToMenuBCBadPos(playerColor, gameID, isInvalidPos);
            }
        }

        checkIfRowAndColAreRightSize(x < 1 || x > 8 || y < 1 || y > 8, playerColor, gameID, isInvalidPos);

        ChessPosition inputPos = new ChessPosition(y, x);
        ChessBoard board = chessPiecePositions().getBoard();
        ChessPiece piece = board.getPiece(inputPos);
        if(piece == null){
            System.out.println("Error: there is no chess piece at that position.");

        } else {
            Collection<ChessPosition> chessPositions = board.getChessPositions();
            for (ChessPosition position : chessPositions) {
                if (position.getRow() == inputPos.getRow() && position.getColumn() == inputPos.getColumn()) {
                    //  make this code grab the most up-to-date chessboard/chess game
                    DrawHighlightedChessBoard drawChessboard =
                            new DrawHighlightedChessBoard(chessPiecePositions(), playerColor);
                    drawChessboard.runHighlight(inputPos);
                }
            }
        }
        returnToGameMenu(playerColor, gameID);
    }

    private void returnToMenuBCBadPos(String playerColor, int gameID, String isInvalidPos) throws ResponseException {
        System.out.println(isInvalidPos);
        // for testing purposes ONLY. Replace these values with the values from the gamePlayMenu later
        returnToGameMenu(playerColor, gameID);
    }

    private void resign(String playerColor, int gameID) throws ResponseException{
        // prompts the user to confirm they want to resign.
        // If they do, the user forfeits the game and the game is over.

        System.out.println("Are you sure you want to resign? (Y/N): ");
        // Does not cause the user to leave the game.
        String input = scanner.nextLine().toUpperCase();
        if(input.equals("Y") || input.equals("YES")){
            // user has resigned
            if(isObserver){
                System.out.println("Error: You can't resign if you are not playing.");
                returnToGameMenu(playerColor, gameID);
            } else {
                try {
                    WebsocketCommunicator ws = new WebsocketCommunicator(this);
                    ws.resignFromGame(auth, gameID);
                    if (isSarcasticText) {
                        System.out.println("The game is like, OVER. Excelsi-whatever.");
                    } else {
                        System.out.println("Game over. Thanks for playing!");
                    }
                } catch (Exception e) {
                    //displayError(new ErrorMessage(e.getMessage()));
                    displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
                }
                returnToGameMenu(playerColor, gameID);
            }

        } else if(input.equals("N") || input.equals("NO")){
            returnToGameMenu(playerColor, gameID);
        } else {
            System.out.println("Error: Invalid option");
            returnToGameMenu(playerColor, gameID);
        }
    }

    private void leave(int gameID) throws ResponseException{
        // removes the user from the game (whether they are playing or observing the game).
        // The client transitions back to the Post-Login UI.
        try {
            WebsocketCommunicator ws = new WebsocketCommunicator(this);
             ws.leaveGamePlayMode(auth, gameID);
        } catch (Exception e) {
            //displayError(new ErrorMessage(e.getMessage()));
            displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
        }
        isPlayingGame = false;
        isObserver = false;
        loggedInHelp();
        loggedIn();
    }

    private void gamePlayHelp() throws ResponseException{
        System.out.println("Enter 1 to redraw the game board.");
        System.out.println("Enter 2 to make a move.");
        System.out.println("Enter 3 to highlight all legal moves for a specific chess piece.");
        System.out.println("Enter 4 to forfeit, ending the game.");
        System.out.println("Enter 5 to leave the game.");
        System.out.println("Enter 6 to see this message again.\n");
        displayGamePlayMenu();
    }

    @Override
    //public void notify(ServerMessage serverMessage) {
    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(message);
            // create a subclass
            case ERROR -> displayError(message);
            case LOAD_GAME -> loadGame(message);
        }
    }

    //private void loadGame(Game gameClass) {
    private void loadGame(String serverMessage){
        LoadGameMessage message =  new Gson().fromJson(serverMessage, LoadGameMessage.class);
        Game gameClass = message.getGame();
        ChessGame game = gameClass.game;
        setChessGame(game);
        String playerColor = gameClass.playerColor;
        if(playerColorClient.equals(playerColor)) {
            DrawChessboard drawChessboard = new DrawChessboard(game, playerColor);
            drawChessboard.run();
        } else {
            DrawChessboard drawChessboard = new DrawChessboard(game, playerColorClient);
            drawChessboard.run();
        }
    }

    //private void displayNotification(NotificationMessage serverMessage) {
    private void displayNotification(String message) {
        NotificationMessage serverMessage = new Gson().fromJson(message, NotificationMessage.class);
        System.out.print(SET_TEXT_COLOR_GREEN);
        System.out.println(serverMessage.getMessage());
        System.out.print(RESET_TEXT_COLOR);
    }

    //public void displayError(ErrorMessage errorMessage){
    public void displayError(String message){
        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
        System.out.print(SET_TEXT_COLOR_RED);
        System.out.println(errorMessage.getErrorMessage());
        System.out.print(RESET_TEXT_COLOR);
    }

    // create matrix for chess piece locations
    public ChessGame chessPiecePositions() {
        // note: this may be a temporary solution, as it may or may not be compatible with Phase 6
        // for now though, it works fine

        ChessGame game = getChessGame();
        return game;
    }
}