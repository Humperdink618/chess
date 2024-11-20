package server;

import com.google.gson.Gson;
import exceptions.UnauthorizedException;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


public class WebSocketRequestHandler {

    private HashMap<Integer, HashSet<Session>> sessionCollection = new HashMap<Integer, HashSet<Session>>();
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
            // String username = getUsername(command.getAuthToken());
            // TODO: create a method or a class that keeps track of all the sessions (probably a collection would be
            //  easiest).
            //saveSession(command.getGameID(), session);
            switch (command.getCommandType()) {
                // TODO: create a method that implements all of these commands for each commandType
                // case CONNECT -> connect(session, username, (ConnectCommand) command) ;
                // -- add user to the collection of sessions and send a message to everyone else that that player
                //     has joined the game. Use send() method.
                // case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                // case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                // case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        //} catch (UnauthorizedException e) {
            // Serializes and sends the error message
            // sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception e){
     //       sendMessage(session.getRemote(), new ErrorMessage("Error: " + e.getMessage()));
        }
    }
}
