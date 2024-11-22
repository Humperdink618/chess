package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.Game;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.HashMap;


public class WebSocketRequestHandler {

    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public WebSocketRequestHandler(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private HashMap<Integer, ConnectionManager> sessionCollection = new HashMap<>();
    // private HashMap<Integer, HashSet<Sessions> sessionCollection = new HashMap<Integer, HashSet<Sessions>();
    // key is gameID, mapped to a set of Sessions
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
            // String username = getUsername(command.getAuthToken());
            // TODO: create a method or a class that keeps track of all the sessions (probably a collection would be
            //  easiest).

            //saveSession(command.getGameID(), session);
            switch (command.getCommandType()) {
                // TODO: create a method that implements all of these commands for each commandType
                // case CONNECT -> connect(session, username, (ConnectCommand) command) ;
                case CONNECT -> connect(session, username, command) ;
                // -- add user to the collection of sessions and send a message to everyone else that that player
                //     has joined the game. Use send() method.
                // case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                // case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case LEAVE -> leaveGame(session, username, command);
                // case RESIGN -> resign(session, username, (ResignCommand) command);
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
            saveSession(command.getGameID(), userSessions);
        }
        userSessions.add(username, session);
        GameData gameData = gameDAO.getGame(command.getGameID());
        String message = "";
        String playerColor = "";
        if(gameData.blackUsername().equals(username)){
            message = String.format("%s has joined the game, playing as Black", username);
            playerColor = "BLACK";
        } else if(gameData.whiteUsername().equals(username)){
            message = String.format("%s has joined the game, playing as White", username);
            playerColor = "WHITE";
        } else {
            message = String.format("%s has joined the game as an observer", username);
            playerColor = "WHITE";
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

    public void leaveGame(Session session, String username, UserGameCommand command) throws Exception{
        ConnectionManager userSessions = sessionCollection.get(command.getGameID());
        if(userSessions == null){
            sendMessage(session, new Gson().toJson(new ErrorMessage("Error: you can't leave if you weren't here to " +
                    "begin with")));
        }
        GameData oldGD = gameDAO.getGame(command.getGameID());
        if(oldGD.blackUsername().equals(username)){
            GameData newGD
                    = new GameData(
                            oldGD.gameID(),
                            oldGD.whiteUsername(),
                            null,
                            oldGD.gameName(),
                            oldGD.game()
                    );
            gameDAO.updateGame(newGD);
        } else if(oldGD.whiteUsername().equals(username)){
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
        userSessions.remove(username);
        String message = String.format("%s has left the game", username);
        NotificationMessage notificationMessage = new NotificationMessage(message);
        userSessions.broadcastToAllButRootClient(username, notificationMessage);
    }

    public void sendMessage(Session session, String msg) throws Exception {
        session.getRemote().sendString(msg);
    }
}
