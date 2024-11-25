package ui.serverfacade;

import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.ResponseException;
import ui.ServerMessageObserver;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import javax.websocket.ContainerProvider;
import javax.websocket.*;
import javax.websocket.MessageHandler;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

public class WebsocketCommunicator extends Endpoint {

    public ServerMessageObserver observer;
    public Session session;

    public WebsocketCommunicator(ServerMessageObserver observer) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.observer = observer;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            // may edit this further as I continue to test my server and websocket stuff.
            public void onMessage(String message) {
                try {

                   // ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                   // observer.notify(serverMessage);
                    observer.notify(message);
                } catch(Exception e) {
                    //observer.notify(new Gson().fromJson(message, ErrorMessage.class));
                    observer.notify(new Gson().toJson(message, ErrorMessage.class));
                    //observer.notify("Error: " + e.getMessage());
                }
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterGamePlayMode(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void leaveGamePlayMode(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            send(new Gson().toJson(command));
            this.session.close();
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void resignFromGame(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clientMakeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
             MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);

            send(new Gson().toJson(command));

        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
