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

public class ChessClient implements ServerMessageObserver {
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
    private String clientColor = null;
    private Integer gameID = null;
    private Boolean isObserver = false;

    public ChessClient(String serverURL){
        serverFacade = new ServerFacade(serverURL);
        this.serverURL = serverURL;
    }

    public void run() throws ResponseException{
        System.out.println("Welcome to 240 chess!");
        ClientWareHouse.notLoggedInHelp();
        while (true) {
            if (isLoggedIn) {
                if(isPlayingGame){
                    returnToGameMenu(getClientColor(), getGameID());
                } else {
                    loggedInMenu();
                }
            } else {
                boolean quit = notLoggedIn();
                if(quit){
                    return;
                }
            }
        }
    }

    private void loggedInMenu() throws ResponseException {
        ClientWareHouse.loggedInHelp();
        loggedIn();
    }

    protected Boolean notLoggedIn() throws ResponseException{
         if(bootUser) {
             return true;
         }
        String input = scanner.nextLine();
        switch (input) {
            case "1" -> loginUser();
            case "2" -> {
                if (isSarcasticText) {
                    SarcasticClient.sarcasticRegister();
                    isSarcasticText = false;
                    counter = 0;
                }
                registerUser();
            } case "3" -> {
                counter = 0;
                isSarcasticText = false;
                return true;
            } case "4" -> {
                if (isSarcasticText) {
                    SarcasticClient.notLoggedInHelpSarcastic();
                }
                ClientWareHouse.notLoggedInHelp();
            } default -> {
                System.out.println("Not a valid option" + (isSarcasticText ? ", idiot!\n" : ".\n"));
                ClientWareHouse.notLoggedInHelp();
            }
        }
        return false;
    }

    public String getClientColor() {
        return clientColor;
    }

    public void setClientColor(String clientColor) {
        this.clientColor = clientColor;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    private void loginUser() throws ResponseException{
        String inputUN;
        String inputPW;
        if(isSarcasticText){
            inputUN = SarcasticClient.getUNSarcastic(scanner);
            inputPW = SarcasticClient.getPWSarcastic(scanner);
        } else {
            inputUN = ClientWareHouse.getUn(scanner);
            inputPW = ClientWareHouse.getPw(scanner);
        }
        String authToken = serverFacade.login(inputUN,inputPW);
        if(authToken.contains("message")){
            ClientWareHouse.getErrorMessageLogin(authToken);
            String inputAnswer = ClientWareHouse.getTryAgain(scanner);
            if(inputAnswer.equals("1")){
                loginUser();
            } else if(inputAnswer.equals("2")){
                notLoggedInMenu();
            } else {
                if(isSarcasticText){
                    SarcasticClient.sarcasticLogin(counter);
                    if(counter < 10){
                        counter += 1;
                        ClientWareHouse.notLoggedInHelp();
                    } else{
                        bootUser = true;
                    }
                    notLoggedIn();
                } else {
                    SarcasticClient.ohSoYouThinkYouAreFunnyEh();
                    counter += 1;
                    if (counter >= 3) {
                        isSarcasticText = true;
                    }
                    notLoggedInMenu();
                }
            }
        } else {
            if(!isSarcasticText){
                counter = 0;
            }
            loginOrRegisterSuccess(authToken, "Login successful!");
        }
    }

    private void notLoggedInMenu() throws ResponseException {
        ClientWareHouse.notLoggedInHelp();
        notLoggedIn();
    }

    private void registerUser() throws ResponseException{
        String inputUN = ClientWareHouse.getInputRegisterCredentials(ClientWareHouse.getUNPrompt(), scanner);
        String inputPW = ClientWareHouse.getInputRegisterCredentials(ClientWareHouse.getPWPrompt(), scanner);
        String inputEmail = ClientWareHouse.getInputRegisterCredentials(ClientWareHouse.getEmailPrompt(), scanner);
        String authToken = serverFacade.register(inputUN,inputPW, inputEmail);
        if(authToken.contains("message")){
            ClientWareHouse.printErrorMessageAuth(authToken);
            notLoggedInMenu();
        } else {
            loginOrRegisterSuccess(authToken, "Register successful!");
        }
    }

    private void loginOrRegisterSuccess(String authToken, String x) {
        auth = authToken;
        System.out.println(x);
        isLoggedIn = true;
    }

    private Boolean loggedIn() throws ResponseException{
        String input = scanner.nextLine();
        switch (input) {
            case "1" -> createGame();
            case "2" -> listGames();
            case "3" -> playGame();
            case "4" -> observeGame();
            case "5" -> {
                logoutUser();
                isSarcasticText = false;
                counter = 0;
                isPlayingGame = false;
                isObserver = false;
                return false;
            }
            case "6" -> ClientWareHouse.loggedInHelp();
           // case "7" -> clearDB(); // DELETE THIS LINE; code for this may be found in SarcasticClient.java

            default -> System.out.println("Not a valid option" + (isSarcasticText ? ", idiot!\n" : ".\n"));
        }
        return true;
    }

    private void createGame() throws ResponseException{
        String gameName = SarcasticClient.promptAndGetGameName(isSarcasticText, scanner);
        // plug in the authToken given from the register/login
        String gameID = serverFacade.create(gameName, auth);
        ClientWareHouse.checkIfGameSuccessfullyCreated(gameID);
        loggedInMenu();
    }

    private void listGames() throws ResponseException{
        // call server facade list game. Plug in the authToken given from the register/login
        String listString = serverFacade.list(auth);
        //  next get response back and store in a variable
        if(listString.contains("message")){
            String errorMessage = ClientWareHouse.getErrorMessage(listString);
            printErrorMessageAndReturnToMenu(errorMessage);
        }
        ListResult listResult = new Gson().fromJson(listString, ListResult.class);
        Collection<GameData> gameList = listResult.games();
        //  check the variable to see if the list game was successful. store gameID but don't print it out
        if(gameList == null || gameList.isEmpty()){
            printErrorMessageAndReturnToMenu("No available games to display.");
        } else {
            System.out.println("Here are all the available games" + (isSarcasticText ? ", or whatever: " : ": "));
            HashMap<Integer, String> gameMap = new HashMap<>();
            for (GameData game : gameList) {
                StringBuilder individualGameData = ClientWareHouse.getIndividualGameData(game);
                gameIDs.add(game.gameID());
                gameDataList.add(game);
                gameMap.put(game.gameID(), individualGameData.toString());
            }
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < gameMap.size(); i++) {
                result.append(i + 1 + ". " + gameMap.get(i+1)).append('\n');
            }
            System.out.println(result);
        }
    }

    private void printErrorMessageAndReturnToMenu(String errorMessage) throws ResponseException {
        System.out.println(errorMessage);
        loggedInMenu();
    }

    private void playGame() throws ResponseException {
        listGames();
        String gameID = ClientWareHouse.getInputGameID(scanner, gameIDs);
        Integer newID = 0;
        newID = ClientWareHouse.getNewGameID(gameID, newID, gameIDs, gameDataList);
        String playerColor = ClientWareHouse.getPlayerColor(scanner);
        // check to see if that team color is taken or not. plug in the authToken given from the register/login
        String joinMessage = serverFacade.join(auth, newID, playerColor);
        if(joinMessage.equals("join successful!")){
            System.out.println("Join successful!");
            try {
                enterGamePlayModeWS(newID);
                setClientColor(playerColor);
            } catch (Exception e) {
                ClientWareHouse.displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
            }
            returnToGameMenu(playerColor, newID);
        } else {
            ClientWareHouse.printErrorMessageJoinGame(joinMessage);
            loggedInMenu();
        }
    }

    private void enterGamePlayModeWS(Integer newID) throws Exception {
        WebsocketCommunicator ws = new WebsocketCommunicator(this);
        ws.enterGamePlayMode(auth, newID);
        isPlayingGame = true;
        setGameID(newID);
    }

    private void observeGame() throws ResponseException{
        // print out the list of games with associated numbers starting at 1 (independent of gameID)
        listGames();
        String observePrompt = ClientWareHouse.getObservePrompt();
        String gameName = scanner.nextLine();
        // note: no calling the ServerFacade here. The Client keeps track of which number is associated with which game
        gameName = ClientWareHouse.getGameNameAgainBecauseInvalid(gameName, scanner);
        String gameIDString = ClientWareHouse.checkIfValidGameID(gameName, observePrompt, gameIDs, scanner);
        int gameID = Integer.parseInt(gameIDString);
        try {
            enterGamePlayModeWS(gameID);
            isObserver = true;
            setClientColor("WHITE");
            System.out.println("You are now observing the game.");
        } catch (Exception e) {
            ClientWareHouse.displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
        }
        returnToGameMenu("WHITE", gameID);
    }

    private void logoutUser() throws ResponseException {
        String logoutMessage = serverFacade.logout(auth);
        if(logoutMessage.equals("logout successful!")){
            System.out.println("Logout successful!" + (isSarcasticText ? " Good riddance..." : ""));
            auth = null;
            isLoggedIn = false;
            ClientWareHouse.notLoggedInHelp();
        } else {
            String errorMessage = ClientWareHouse.getErrorMessage(logoutMessage);
            ClientWareHouse.printUnauthorizedErrorMessage(errorMessage);
            loggedInMenu();
        }
    }

    private Boolean gameMenu(String playerColor, int gameID) throws ResponseException{
        String input = scanner.nextLine();
        switch (input) {
            case "1" -> redrawChessBoard(playerColor, gameID);
            case "2" -> makeMove(playerColor, gameID);
            case "3" -> highlightLegalMoves(playerColor, gameID);
            case "4" -> resign(playerColor, gameID);
            case "5" -> {
                leave(gameID);
                return false;
            }
            case "6" -> {
                ClientWareHouse.gamePlayHelp();
                gameMenu(playerColor, gameID); // possibly may not need this line
            }
            default -> System.out.println("Not a valid option.\n");
        }
        return true;
    }

    private void redrawChessBoard(String playerColor, int gameID) throws ResponseException {
        // redraws the current chessboard
        DrawChessboard drawChessboard = new DrawChessboard(getChessGame(), playerColor);
        drawChessboard.run();
        returnToGameMenu(playerColor, gameID);
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    private void makeMove(String playerColor, int gameID) throws ResponseException{
        // makes a move on the ChessBoard during a game
        if(isObserver){
            ClientWareHouse.displayInvalidMoveObserverMessage();
            returnToGameMenu(playerColor, gameID);
        }
        String prompt1 = ClientWareHouse.promptForStartPos();
        String isInvalidPos = ClientWareHouse.getInvalidPosMessage();
        ChessPosition startPos = getInputChessPosition(prompt1, playerColor, isInvalidPos);
        String prompt2 = ClientWareHouse.promptForEndPos();
        ChessPosition endPos = getInputChessPosition(prompt2, playerColor, isInvalidPos);
        ChessBoard board = getChessGame().getBoard();
        ChessPiece chessPiece = board.getPiece(startPos);
        Boolean canPromote = false;
        if(chessPiece == null){
            returnToMenuBCBadPos(playerColor, gameID, isInvalidPos);
        } else if(chessPiece.getPieceType() == ChessPiece.PieceType.PAWN){
            canPromote = ClientWareHouse.checkIfCanPromote(chessPiece, endPos);
        }
        ChessPiece promotionPiece = ClientWareHouse.getPromotionPieceClient(canPromote, chessPiece, scanner);
        ChessMove move = ClientWareHouse.getMove(promotionPiece, startPos, endPos);
        try {
            WebsocketCommunicator ws = new WebsocketCommunicator(this);
            ws.clientMakeMove(auth, gameID, move);
        } catch (Exception e) {
            ClientWareHouse.displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
        }
        returnToGameMenu(playerColor, gameID);
    }

    private void areRowAndColRightSize(boolean x, String color, int gameID, String badPos) throws ResponseException {
        if (x) {
            returnToMenuBCBadPos(color, gameID, badPos);
        }
    }

    public ChessPosition getInputChessPosition(String prompt, String color, String badPos) throws ResponseException {
        System.out.println(prompt);
        String inputStartPos = scanner.nextLine().toLowerCase();
        String startPrompt = ClientWareHouse.getStartPrompt();
        inputStartPos = ClientWareHouse.getChessPosAgain(inputStartPos, startPrompt, scanner);
        return getNewChessPosition(color, badPos, inputStartPos);
    }

    private ChessPosition getNewChessPosition(String color, String badPos, String inputPos) throws ResponseException {
        char[] inputCharStartPos = inputPos.toCharArray();
        isCharArrayGoodInput(color, gameID, inputCharStartPos, badPos);
        int i = 0;
        int j = 0;
        for(char c : inputCharStartPos){
            if(Character.isLetter(c)){
                i = c - 'a' + 1;
            } else if(Character.isDigit(c)){
                j = Character.getNumericValue(c);
            } else {
                returnToMenuBCBadPos(color, gameID, badPos);
            }
        }
        areRowAndColRightSize(i < 1 || i > 8 || j < 1 || j > 8, color, gameID, badPos);
        return new ChessPosition(j, i);
    }

    private void returnToGameMenu(String playerColor, int gameID) throws ResponseException {
        ClientWareHouse.displayGamePlayMenu();
        gameMenu(playerColor, gameID);
    }

    private void highlightLegalMoves(String playerColor, int gameID) throws ResponseException{
        // highlights all legal moves a chess piece can make on a ChessBoard during a game
        String hLPrompt = ClientWareHouse.getHLPrompt();
        String chessPos = ClientWareHouse.getChessPos(scanner, hLPrompt);
        chessPos = ClientWareHouse.getChessPosAgain(chessPos, hLPrompt, scanner);
        String isInvalidPos = ClientWareHouse.getInvalidPosMessage();
        ChessPosition inputPos = getNewChessPosition(playerColor, isInvalidPos, chessPos);
        ChessBoard board = getChessGame().getBoard();
        ChessPiece piece = board.getPiece(inputPos);
        if(piece == null){
            System.out.println("Error: there is no chess piece at that position.");
        } else {
            ClientWareHouse.drawHighlightedBoard(playerColor, board, inputPos, getChessGame());
        }
        returnToGameMenu(playerColor, gameID);
    }

    private void isCharArrayGoodInput(String color, int id, char[] charPos, String badPos) throws ResponseException {
        for(int i = 0; i < charPos.length; i++){
            if(!Character.isLetter(charPos[0]) || !Character.isDigit(charPos[1])) {
                returnToMenuBCBadPos(color, id, badPos);
            } else {
                areRowAndColRightSize(charPos.length > 2, color, id, badPos);
            }
        }
    }

    private void returnToMenuBCBadPos(String playerColor, int gameID, String isInvalidPos) throws ResponseException {
        returnToGameMenuBCInvalidOption(isInvalidPos, playerColor, gameID);
    }

    private void resign(String color, int gameID) throws ResponseException{
        // prompts the user to confirm they want to resign. If they do, the user forfeits the game and the game is over.
        String input = ClientWareHouse.inputDoYouWantToResign(scanner);
        if(input.equals("Y") || input.equals("YES")){
            if(isObserver){
                returnToGameMenuBCInvalidOption("Error: You can't resign if you are not playing.", color, gameID);
            } else {
                try {
                    WebsocketCommunicator ws = new WebsocketCommunicator(this);
                    ws.resignFromGame(auth, gameID);
                    SarcasticClient.printEndGameMessage(isSarcasticText);
                } catch (Exception e) {
                    ClientWareHouse.displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
                }
                returnToGameMenu(color, gameID);
            }
        } else if(input.equals("N") || input.equals("NO")){
            returnToGameMenu(color, gameID);
        } else {
            returnToGameMenuBCInvalidOption("Error: Invalid option", color, gameID);
        }
    }

    private void returnToGameMenuBCInvalidOption(String x, String playerColor, int gameID) throws ResponseException {
        System.out.println(x);
        returnToGameMenu(playerColor, gameID);
    }

    private void leave(int gameID) throws ResponseException{
        // removes the user from the game (players and observers). The client transitions back to the Post-Login UI.
        try {
            WebsocketCommunicator ws = new WebsocketCommunicator(this);
             ws.leaveGamePlayMode(auth, gameID);
        } catch (Exception e) {
            ClientWareHouse.displayError(new Gson().toJson(e.getMessage(), ErrorMessage.class));
        }
        isPlayingGame = false;
        isObserver = false;
        loggedInMenu();
    }
    @Override
    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> ClientWareHouse.displayNotification(message);
            // create a subclass
            case ERROR -> ClientWareHouse.displayError(message);
            case LOAD_GAME -> loadGame(message);
        }
    }

    private void loadGame(String serverMessage){
        LoadGameMessage message =  new Gson().fromJson(serverMessage, LoadGameMessage.class);
        Game gameClass = message.getGame();
        ChessGame game = gameClass.game;
        setChessGame(game);
        String color = gameClass.playerColor;
        DrawChessboard board = (clientColor.equals(color) ? new DrawChessboard(game, color) :
                new DrawChessboard(game, clientColor));
        board.run();
    }
}