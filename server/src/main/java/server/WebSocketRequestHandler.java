package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.util.HashMap;

@WebSocket
public class WebSocketRequestHandler {

    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public WebSocketRequestHandler(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    // key is gameID, mapped to a set of Sessions
    private HashMap<Integer, ConnectionManager> sessionCollection = new HashMap<>();

    // every time a player joins or observes, websocket automatically creates a session. Just have to add that session
    // to a collection, and pass that into a map.

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception, UnauthorizedException {
        // need some structure on the server side to keep track of players (i.e. games to players.)
        // can grab all those sessions and send messages to those players and observers.
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            // Throws a custom UnauthorizedException. Yours may work differently.
            String authToken = command.getAuthToken();
            if(authDAO.getAuth(authToken) == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();
            GameData gameData = gameDAO.getGame(command.getGameID());
            Integer gameID = gameData.gameID();
            if(gameID == null){
                throw new UnauthorizedException("Error: game doesn't exist");
            }

            // create a method or a class that keeps track of all the sessions (probably a collection would be
            //  easiest).

            switch (command.getCommandType()) {
                //  create a method that implements all of these commands for each commandType
                case CONNECT -> connect(session, username, command) ;
                // -- add user to the collection of sessions and send a message to everyone else that that player
                //     has joined the game. Use send() method.
                case MAKE_MOVE -> makeMove(session, username, message);
                //case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (UnauthorizedException e) {
            // Serializes and sends the error message
             sendMessage(session, new Gson().toJson(new ErrorMessage("Error: unauthorized")));
        } catch (Exception e){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    public void connect(Session session, String username, UserGameCommand command) throws Exception{

        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            ConnectionManager newUserSessions = new ConnectionManager();
            userSessions = newUserSessions;
            saveSession(command.getGameID(), userSessions);
        }
        userSessions.add(username, session);
        GameData gameData = gameDAO.getGame(command.getGameID());
        String message = "";
        String playerColor = "";
        if(gameData.blackUsername() == null || gameData.whiteUsername() == null){
            if(gameData.whiteUsername() != null){
                if(gameData.whiteUsername().equals(username)) {
                    message = String.format("%s has joined the game, playing as White", username);
                    playerColor = "WHITE";
                } else {
                    message = String.format("%s has joined the game as an observer", username);
                    playerColor = "WHITE";
                }
            } else if(gameData.blackUsername() != null){
                if(gameData.blackUsername().equals(username)) {
                    message = String.format("%s has joined the game, playing as Black", username);
                    playerColor = "BLACK";
                } else {
                    message = String.format("%s has joined the game as an observer", username);
                    playerColor = "WHITE";
                }
            } else if(gameData.blackUsername() == null && gameData.whiteUsername() == null){
                message = String.format("%s has joined the game as an observer", username);
                playerColor = "WHITE";
            }

        } else if(gameData.whiteUsername() != null && gameData.blackUsername() != null){
            if(gameData.blackUsername().equals(username)) {
                message = String.format("%s has joined the game, playing as Black", username);
                playerColor = "BLACK";
            } else if(gameData.whiteUsername().equals(username)) {
                message = String.format("%s has joined the game, playing as White", username);
                playerColor = "WHITE";
            } else {
                message = String.format("%s has joined the game as an observer", username);
                playerColor = "WHITE";
            }
        }
        Game game = new Game(gameData.game(), playerColor);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        userSessions.broadcastToRootClient(username, loadGameMessage);
        NotificationMessage notificationMessage = new NotificationMessage(message);
        userSessions.broadcastToAllButRootClient(username, notificationMessage);
    }

    public void saveSession(int gameID, ConnectionManager userSessions){
        sessionCollection.put(gameID, userSessions);
    }

    public void resign(Session session, String username, UserGameCommand command) throws Exception{
        // sets the game to game over
        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't resign if you were never part" +
                    "of the game")));
        }
        GameData oldGD = gameDAO.getGame(command.getGameID());
        if(oldGD.whiteUsername() == null || oldGD.blackUsername() == null) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you cannot resign if you're the" +
                    " only one playing (or if you are simply observing).")));
        } else if (!oldGD.blackUsername().equals(username) && !oldGD.whiteUsername().equals(username)) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you cannot resign if you are " +
                            "observing.")));
        } else if(oldGD.game().isGameOver()){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: The game is already over. No need to " +
                    "resign.")));
        } else {

            ChessGame chessGame = oldGD.game();
            chessGame.setGameOver(true);
            gameDAO.updateGame(new GameData(
                    oldGD.gameID(),
                    oldGD.whiteUsername(),
                    oldGD.blackUsername(),
                    oldGD.gameName(),
                    chessGame)
            );
            String message = String.format("%s has resigned. GAME OVER. Thanks for playing!", username);
            NotificationMessage notificationMessage = new NotificationMessage(message);
            userSessions.broadcastToAll(notificationMessage);
        }
    }

    public void makeMove(Session session, String uN, String message) throws Exception{
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        String authToken = command.getAuthToken();
        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData oldGD = gameDAO.getGame(command.getGameID());
        Integer gameID = oldGD.gameID();
        if(gameID == null){
            throw new UnauthorizedException("Error: game doesn't exist");
        }
        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't make a move if you're " +
                    "not in the game!")));
        }
        if(oldGD.whiteUsername() == null || oldGD.blackUsername() == null) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you cannot make a move if you're not " +
                    "playing against anyone.")));
        } else if (!oldGD.blackUsername().equals(uN) && !oldGD.whiteUsername().equals(uN)) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you cannot make a move if you are " +
                    "observing.")));
        } else if(oldGD.game().isGameOver()){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: The game is already over. No more moves" +
                    " can be made.")));
        } else {
            makeMoveGame(session, uN, oldGD, command, userSessions);
        }
    }

    private void makeMoveGame(Session session,
                              String uN,
                              GameData oldGD,
                              MakeMoveCommand command,
                              ConnectionManager userSessions) throws Exception {
        ChessGame chessGame = oldGD.game();
        ChessMove move = command.getMove();
        ChessBoard board = chessGame.getBoard();
        ChessPiece chessPiece = board.getPiece(move.getStartPosition());
        String playerColor = "";
        String moveMessageMajor = ""; // check, checkmate, and stalemate
        String moveMessage;
        ChessGame.TeamColor teamColor = chessPiece.getTeamColor();
        ChessGame.TeamColor enemyTeamColor = null;
        String enemyUserName = "";
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        int startRow = startPos.getColumn();
        int startCol = startPos.getRow();
        int endRow = endPos.getColumn();
        int endCol = endPos.getRow();
        String sRowLett = ""; // start row letter
        String eRowLett = ""; // end row letter
        String wUN = oldGD.whiteUsername();
        String bUN = oldGD.blackUsername();
        int myGameID = oldGD.gameID();
        if(teamColor == ChessGame.TeamColor.WHITE) {
            for (int i = 1; i < 9; i++) {
                sRowLett = convertToInt(i, startRow, sRowLett);
                eRowLett = convertToInt(i, endRow, eRowLett);
            }
        } else {
            for (int i = 8; i > -1; i--) {
                sRowLett = convertToInt(i, startRow, sRowLett);
                eRowLett = convertToInt(i, endRow, eRowLett);
            }
        }
        if(sRowLett.isBlank() || eRowLett.isBlank()){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: invalid move.")));
        }
        try {
            if(wUN.equals(uN)){
                if(teamColor != ChessGame.TeamColor.WHITE) {
                    throw new InvalidMoveException("you can't move an opponent's piece.");
                } else if(chessGame.getTeamTurn() != teamColor){
                    throw new InvalidMoveException("it's not your turn.");
                } else {
                    playerColor = "WHITE";
                    enemyTeamColor = ChessGame.TeamColor.BLACK;
                    enemyUserName = bUN;
                }
            } else if(bUN.equals(uN)){
                if(teamColor != ChessGame.TeamColor.BLACK){
                    throw new InvalidMoveException("you can't move an opponent's piece.");
                } else if(chessGame.getTeamTurn() != teamColor){
                    throw new InvalidMoveException("it's not your turn.");
                } else {
                    playerColor = "BLACK";
                    enemyTeamColor = ChessGame.TeamColor.WHITE;
                    enemyUserName = wUN;
                }
            }
                chessGame.makeMove(move);
                moveMessage = String.format("%s moved a piece from %s%s to %s%s",
                        uN, sRowLett, startCol, eRowLett, endCol);
                if(move.getPromotionPiece() != null){
                    moveMessage = String.format("%s moved a PAWN from %s%s to %s%s and promoted it to a %s",
                            uN, sRowLett, startCol, eRowLett, endCol, move.getPromotionPiece());
                }
                if(chessGame.isInCheck(enemyTeamColor) && !chessGame.isInCheckmate(enemyTeamColor)){
                    moveMessageMajor = String.format("%s has put %s's king in Check", uN, enemyUserName);
                }
                if(chessGame.isInCheckmate(enemyTeamColor)) {
                    moveMessageMajor = String.format("GAME OVER: %s has put %s's king in Checkmate. " +
                            "%s Team WINS! Thank you for playing!", uN, enemyUserName, teamColor);
                    chessGame.setGameOver(true);
                } else if(chessGame.isInStalemate(enemyTeamColor)){
                    moveMessageMajor = "GAME OVER: game ends in Stalemate. No one wins. Better luck next time!";
                    chessGame.setGameOver(true);
                }
            gameDAO.updateGame(new GameData(myGameID, wUN, bUN, oldGD.gameName(), chessGame));
            sendLoadGameMessageToAll(chessGame, playerColor, userSessions);
            sendNotificationPlayerMadeMove(uN, moveMessage, userSessions);
            if(chessGame.isInCheck(enemyTeamColor) && !chessGame.isInCheckmate(enemyTeamColor)){
                sendNotificationToAll(moveMessageMajor, userSessions);
            } else if(chessGame.isInCheckmate(enemyTeamColor) || chessGame.isInStalemate(enemyTeamColor)){
                sendNotificationToAll(moveMessageMajor, userSessions);
            }
        } catch (InvalidMoveException e) {
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private static void sendNotificationPlayerMadeMove(String uN, String moveMessage, ConnectionManager userSessions) throws Exception {
        NotificationMessage notifyThatPlayerHasMadeMove = new NotificationMessage(moveMessage);
        userSessions.broadcastToAllButRootClient(uN, notifyThatPlayerHasMadeMove);
    }

    private static void sendLoadGameMessageToAll(ChessGame chessGame, String playerColor, ConnectionManager userSessions) throws Exception {
        Game game = new Game(chessGame, playerColor);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        userSessions.broadcastToAll(loadGameMessage);
    }

    private static void sendNotificationToAll(String message, ConnectionManager userSessions) throws Exception {
        NotificationMessage notifyMajorGamePlayStatus = new NotificationMessage(message);
        userSessions.broadcastToAll(notifyMajorGamePlayStatus);
        // notifies everyone of major gameplay status update (check, checkmate, stalemate)
    }

    private String convertToInt(int i, int startRow, String startRowLetter) {
        if (i == startRow) {
            startRowLetter = getCharForNumber(i);
        }
        return startRowLetter;
    }

    private String getCharForNumber(int i){
        return i > 0 && i < 27 ? String.valueOf((char)(i + 'a' - 1)) : null;
    }

    public void leaveGame(Session session, String username, UserGameCommand command) throws Exception{
        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't leave if you weren't here to " +
                    "begin with")));
        }
        GameData oldGD = gameDAO.getGame(command.getGameID());
        if(oldGD.blackUsername() != null){
            if(oldGD.blackUsername().equals(username)) {
                GameData newGD
                        = new GameData(
                        oldGD.gameID(),
                        oldGD.whiteUsername(),
                        null,
                        oldGD.gameName(),
                        oldGD.game()
                );
                gameDAO.updateGame(newGD);
            }
        }
        if(oldGD.whiteUsername() != null) {
            if (oldGD.whiteUsername().equals(username)) {
                GameData newGD =
                        new GameData(
                                oldGD.gameID(),
                                null,
                                oldGD.blackUsername(),
                                oldGD.gameName(),
                                oldGD.game()
                        );
                gameDAO.updateGame(newGD);
            }
        }
        userSessions.remove(username);
        String message = String.format("%s has left the game", username);
        NotificationMessage notificationMessage = new NotificationMessage(message);
        userSessions.broadcastToAllButRootClient(username, notificationMessage);
    }

    public void sendMessage(Session session, String msg) throws Exception {
        session.getRemote().sendString(msg);
    }
}
